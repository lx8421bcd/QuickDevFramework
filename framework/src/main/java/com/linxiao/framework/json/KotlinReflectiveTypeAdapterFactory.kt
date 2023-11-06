package com.linxiao.framework.json

import com.google.gson.FieldNamingStrategy
import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.google.gson.ReflectionAccessFilter
import com.google.gson.ReflectionAccessFilter.FilterResult
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.internal.`$Gson$Types`
import com.google.gson.internal.ConstructorConstructor
import com.google.gson.internal.Excluder
import com.google.gson.internal.ObjectConstructor
import com.google.gson.internal.Primitives
import com.google.gson.internal.ReflectionAccessFilterHelper
import com.google.gson.internal.bind.JsonAdapterAnnotationTypeAdapterFactory
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory
import com.google.gson.internal.bind.SerializationDelegatingTypeAdapter
import com.google.gson.internal.reflect.ReflectionHelper
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.lang.reflect.AccessibleObject
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import java.util.Collections
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

/**
 * 修改自Gson内原本的ReflectiveTypeAdapterFactory，适配Kotlin Null安全。
 * 使用此方法而非自定义TypeAdapterFactory主要是为了提高性能。
 *
 * 存在以下规则：
 * * 在反序列化时，若反序列化结果不为null则正常赋值
 * * 在反序列化时，若结果为null，如果检测到目标成员有NonNull声明，则不赋值，使用成员定义的默认值
 * * Java实现的类型在参与反序列化时，默认所有成员为NonNull声明(kotlin reflection特性)
 *
 * @author lx8421bcd
 * @since 2023-10-18
 */
class KotlinReflectiveTypeAdapterFactory(
    private val constructorConstructor: ConstructorConstructor,
    private val fieldNamingPolicy: FieldNamingStrategy,
    private val excluder: Excluder,
    private val jsonAdapterFactory: JsonAdapterAnnotationTypeAdapterFactory,
    private val reflectionFilters: List<ReflectionAccessFilter>
) : TypeAdapterFactory {

    private val TAG = this::class.java.simpleName
    private fun includeField(f: Field, serialize: Boolean): Boolean {
        return !excluder.excludeClass(f.type, serialize) && !excluder.excludeField(f, serialize)
    }

    private fun getFieldNames(f: Field): List<String> {
        val annotation = f.getAnnotation(SerializedName::class.java)
        if (annotation == null) {
            val name = fieldNamingPolicy.translateName(f)
            return listOf(name)
        }
        val serializedName = annotation.value
        val alternates = annotation.alternate
        if (alternates.isEmpty()) {
            return listOf(serializedName)
        }
        val fieldNames: MutableList<String> = ArrayList(alternates.size + 1)
        fieldNames.add(serializedName)
        Collections.addAll(fieldNames, *alternates)
        return fieldNames
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        val raw = type.rawType
        if (!Any::class.java.isAssignableFrom(raw)) {
            return null // it's a primitive!
        }
        val filterResult = ReflectionAccessFilterHelper.getFilterResult(
            reflectionFilters, raw
        )
        if (filterResult == FilterResult.BLOCK_ALL) {
            throw JsonIOException("ReflectionAccessFilter does not permit using reflection for $raw"
                    + ". Register a TypeAdapter for this type or adjust the access filter.")
        }
        val blockInaccessible = filterResult == FilterResult.BLOCK_INACCESSIBLE
        // If the type is actually a Java Record, we need to use the RecordAdapter instead. This will always be false
        // on JVMs that do not support records.
        if (ReflectionHelper.isRecord(raw)) {
            return RecordAdapter<T>(
                raw,
                getBoundFields(gson, type, raw, blockInaccessible, true), blockInaccessible
            ) as TypeAdapter<T>
        }
        val constructor = constructorConstructor.get(type)
        return FieldReflectionAdapter(
            constructor,
            getBoundFields(gson, type, raw, blockInaccessible, false)
        ) as TypeAdapter<T>
    }

    private fun createBoundField(
        context: Gson,
        field: Field,
        accessor: Method?,
        name: String,
        fieldType: TypeToken<*>,
        serialize: Boolean,
        deserialize: Boolean,
        blockInaccessible: Boolean,
        isKotlinNullable: Boolean
    ): BoundField {
        val isPrimitive = Primitives.isPrimitive(fieldType.rawType)
        val modifiers = field.modifiers
        val isStaticFinalField = Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)
        val annotation = field.getAnnotation(JsonAdapter::class.java)
        var mapped: TypeAdapter<*>? = null
        if (annotation != null) {
            // This is not safe; requires that user has specified correct adapter class for @JsonAdapter
            mapped = JsonAdapterAnnotationTypeAdapterFactory::class.java
                .getDeclaredMethod("getTypeAdapter")
                .invoke(jsonAdapterFactory, constructorConstructor, context, fieldType, annotation) as TypeAdapter<*>?
//            mapped = jsonAdapterFactory.getTypeAdapter(constructorConstructor, context, fieldType, annotation)
        }
        val jsonAdapterPresent = mapped != null
        if (mapped == null)  {
            mapped = context.getAdapter(fieldType)
        }
        @Suppress("UNCHECKED_CAST") val typeAdapter = mapped as TypeAdapter<Any>?
        return object : BoundField(name, field.name, serialize, deserialize) {
            @Throws(IOException::class, IllegalAccessException::class)
            override fun write(writer: JsonWriter, source: Any) {
                if (!serialized) return
                if (blockInaccessible) {
                    if (accessor == null) {
                        checkAccessible(source, field)
                    } else {
                        // Note: This check might actually be redundant because access check for canonical
                        // constructor should have failed already
                        checkAccessible(source, accessor)
                    }
                }
                val fieldValue = if (accessor != null) {
                    try {
                        accessor.invoke(source)
                    } catch (e: InvocationTargetException) {
                        val accessorDescription =
                            ReflectionHelper.getAccessibleObjectDescription(accessor, false)
                        throw JsonIOException(
                            "Accessor $accessorDescription threw exception",
                            e.cause
                        )
                    }
                } else {
                    field[source]
                }
                if (fieldValue === source) {
                    // avoid direct recursion
                    return
                }
                writer.name(name)
                val t = if (jsonAdapterPresent) typeAdapter else TypeAdapterRuntimeTypeWrapper(
                    context,
                    typeAdapter,
                    fieldType.type
                )
                t!!.write(writer, fieldValue)
            }

            @Throws(IOException::class, JsonParseException::class)
            override fun readIntoArray(reader: JsonReader, index: Int, target: Array<Any?>) {
                val fieldValue = typeAdapter!!.read(reader)
                if (fieldValue == null && isPrimitive) {
                    throw JsonParseException(
                        "null is not allowed as value for record component '" + fieldName + "'"
                                + " of primitive type; at path " + reader.path
                    )
                }
                target[index] = fieldValue
            }

            @Throws(IOException::class, IllegalAccessException::class)
            override fun readIntoField(reader: JsonReader?, target: Any?) {
//                Log.d(TAG, "readIntoField: field =  $fieldName")
//                Log.d(TAG, "readIntoField: isKotlinNonNull = $isKotlinNonNull")
                val fieldValue = typeAdapter!!.read(reader)
                if (fieldValue != null || !isPrimitive) {
                    if (blockInaccessible) {
                        checkAccessible(target, field)
                    } else if (isStaticFinalField) {
                        // Reflection does not permit setting value of `static final` field, even after calling `setAccessible`
                        // Handle this here to avoid causing IllegalAccessException when calling `Field.set`
                        val fieldDescription = ReflectionHelper.getAccessibleObjectDescription(field, false)
                        throw JsonIOException("Cannot set value of 'static final' $fieldDescription")
                    }
                    if (fieldValue != null) {
                        field[target] = fieldValue
                    }
                    else {
                        if (isKotlinNullable) {
                            field[target] = null
                        }
                    }
                }
            }
        }
    }

    private fun getBoundFields(
        context: Gson,
        inputType: TypeToken<*>,
        inputRaw: Class<*>,
        inputBlockInaccessible: Boolean,
        isRecord: Boolean
    ): Map<String?, BoundField> {
        var type = inputType
        var raw = inputRaw
        var blockInaccessible = inputBlockInaccessible
        val result: MutableMap<String?, BoundField> = LinkedHashMap()
        if (raw.isInterface) {
            return result
        }
        val declaredType = type.type
        val originalRaw = raw

//        Log.d(TAG, "getBoundFields: " + raw.name)
        while (raw != Any::class.java) {
            val fields = raw.kotlin.memberProperties
            // For inherited fields, check if access to their declaring class is allowed
            if (raw != originalRaw && fields.isNotEmpty()) {
                val filterResult = ReflectionAccessFilterHelper.getFilterResult(
                    reflectionFilters, raw
                )
                if (filterResult == FilterResult.BLOCK_ALL) {
                    throw JsonIOException(
                        "ReflectionAccessFilter does not permit using reflection for " + raw
                                + " (supertype of " + originalRaw + "). Register a TypeAdapter for this type"
                                + " or adjust the access filter."
                    )
                }
                blockInaccessible = filterResult == FilterResult.BLOCK_INACCESSIBLE
            }
            raw.kotlin.memberProperties.forEach {
                val field = it.javaField ?: raw.getDeclaredField(it.name)
                var serialize = includeField(field, true)
                var deserialize = includeField(field, false)
                if (!serialize && !deserialize) {
                    return@forEach
                }
                var accessor: Method? = null
                if (isRecord) {
                    if (Modifier.isStatic(field.modifiers)) {
                        deserialize = false
                    } else {
                        accessor = ReflectionHelper.getAccessor(raw, field)
                        // If blockInaccessible, skip and perform access check later
                        if (!blockInaccessible) {
                            ReflectionHelper.makeAccessible(accessor)
                        }
                        if (accessor.getAnnotation(SerializedName::class.java) != null
                            && field.getAnnotation(SerializedName::class.java) == null
                        ) {
                            val methodDescription = ReflectionHelper.getAccessibleObjectDescription(accessor, false)
                            throw JsonIOException("@SerializedName on $methodDescription is not supported")
                        }
                    }
                }
                if (!blockInaccessible && accessor == null) {
                    ReflectionHelper.makeAccessible(field)
                }
                val fieldType: Type = `$Gson$Types`.resolve(type.type, raw, field.genericType)
                val fieldNames = getFieldNames(field)
                var previous: BoundField? = null
                for (i in fieldNames.indices) {
                    val name = fieldNames[i]
                    if (i != 0) serialize = false // only serialize the default name
                    val boundField = createBoundField(
                        context,
                        field,
                        accessor,
                        name,
                        TypeToken.get(fieldType),
                        serialize,
                        deserialize,
                        blockInaccessible,
                        it.returnType.isMarkedNullable,
                    )
                    val replaced = result.put(name, boundField)
                    if (previous == null) previous = replaced
                }
                if (previous != null) {
                    declaredType.toString() + " declares multiple JSON fields named " + previous.name
                }
            }
            type = TypeToken.get(`$Gson$Types`.resolve(type.type, raw, raw.genericSuperclass))
            raw = type.rawType
        }
        return result
    }

    abstract class BoundField protected constructor(
        val name: String,
        val fieldName: String, val serialized: Boolean, val deserialized: Boolean
    ) {
        @Throws(IOException::class, IllegalAccessException::class)
        abstract fun write(writer: JsonWriter, source: Any)

        @Throws(IOException::class, JsonParseException::class)
        abstract fun readIntoArray(reader: JsonReader, index: Int, target: Array<Any?>)

        @Throws(IOException::class, IllegalAccessException::class)
        abstract fun readIntoField(reader: JsonReader?, target: Any?)
    }

    abstract class Adapter<T, A> internal constructor(private val boundFields: Map<String?, BoundField>) : TypeAdapter<T?>() {
        @Throws(IOException::class)
        override fun write(out: JsonWriter, value: T?) {
            if (value == null) {
                out.nullValue()
                return
            }
            out.beginObject()
            try {
                for (boundField in boundFields.values) {
                    boundField.write(out, value)
                }
            } catch (e: IllegalAccessException) {
                throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(e)
            }
            out.endObject()
        }

        @Throws(IOException::class)
        override fun read(`in`: JsonReader): T? {
            if (`in`.peek() == JsonToken.NULL) {
                `in`.nextNull()
                return null
            }
            val accumulator = createAccumulator()
            try {
                `in`.beginObject()
                while (`in`.hasNext()) {
                    val name = `in`.nextName()
                    val field = boundFields[name]
                    if (field == null || !field.deserialized) {
                        `in`.skipValue()
                    } else {
                        readField(accumulator, `in`, field)
                    }
                }
            } catch (e: IllegalStateException) {
                throw JsonSyntaxException(e)
            } catch (e: IllegalAccessException) {
                throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(e)
            }
            `in`.endObject()
            return finalize(accumulator)
        }

        abstract fun createAccumulator(): A

        @Throws(IllegalAccessException::class, IOException::class)
        abstract fun readField(accumulator: A, `in`: JsonReader, field: BoundField)

        abstract fun finalize(accumulator: A): T
    }

    private class FieldReflectionAdapter<T> constructor(
        private val constructor: ObjectConstructor<T>,
        boundFields: Map<String?, BoundField>
    ) : Adapter<T, T>(boundFields) {
        override fun createAccumulator(): T {
            return constructor.construct()
        }

        @Throws(IllegalAccessException::class, IOException::class)
        override fun readField(accumulator: T, `in`: JsonReader, field: BoundField) {
            field.readIntoField(`in`, accumulator)
        }

        override fun finalize(accumulator: T): T {
            return accumulator
        }
    }

    private class RecordAdapter<T>(
        raw: Class<in T>,
        boundFields: Map<String?, BoundField>,
        blockInaccessible: Boolean
    ) : Adapter<T, Array<Any?>>(boundFields) {

        companion object {
            val PRIMITIVE_DEFAULTS = primitiveDefaults()
            private fun primitiveDefaults(): Map<Class<*>?, Any> {
                val zeroes: MutableMap<Class<*>?, Any> = HashMap()
                zeroes[Byte::class.javaPrimitiveType] = 0.toByte()
                zeroes[Short::class.javaPrimitiveType] = 0.toShort()
                zeroes[Int::class.javaPrimitiveType] = 0
                zeroes[Long::class.javaPrimitiveType] = 0L
                zeroes[Float::class.javaPrimitiveType] = 0f
                zeroes[Double::class.javaPrimitiveType] = 0.0
                zeroes[Char::class.javaPrimitiveType] = '\u0000'
                zeroes[Boolean::class.javaPrimitiveType] = false
                return zeroes
            }
        }

        private val constructor: Constructor<T>
        private val constructorArgsDefaults: Array<Any?>
        private val componentIndices: MutableMap<String, Int> = HashMap()

        init {
            @Suppress("UNCHECKED_CAST")
            constructor = ReflectionHelper.getCanonicalRecordConstructor(raw) as Constructor<T>
            if (blockInaccessible) {
                checkAccessible(null, constructor)
            } else {
                // Ensure the constructor is accessible
                ReflectionHelper.makeAccessible(constructor)
            }
            val componentNames = ReflectionHelper.getRecordComponentNames(raw)
            for (i in componentNames.indices) {
                componentIndices[componentNames[i]] = i
            }
            val parameterTypes = constructor.parameterTypes
            constructorArgsDefaults = arrayOfNulls(parameterTypes.size)
            for (i in parameterTypes.indices) {
                constructorArgsDefaults[i] = PRIMITIVE_DEFAULTS[parameterTypes[i]]
            }
        }

        override fun createAccumulator(): Array<Any?> {
            return constructorArgsDefaults.clone()
        }

        @Throws(IOException::class)
        override fun readField(accumulator: Array<Any?>, `in`: JsonReader, field: BoundField) {
            // Obtain the component index from the name of the field backing it
            val componentIndex = componentIndices[field.fieldName] ?: throw IllegalStateException(
                "Could not find the index in the constructor '${ReflectionHelper.constructorToString(constructor)}'"
                        + " for field with name '${field.fieldName}',"
                        + " unable to determine which argument in the constructor the field corresponds"
                        + " to. This is unexpected behavior, as we expect the RecordComponents to have the"
                        + " same names as the fields in the Java class, and that the order of the"
                        + " RecordComponents is the same as the order of the canonical constructor parameters."
            )
            field.readIntoArray(`in`, componentIndex, accumulator)
        }

        override fun finalize(accumulator: Array<Any?>): T {
            return try {
                constructor.newInstance(*accumulator)
            } catch (e: IllegalAccessException) {
                throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(e)
            } catch (e: InvocationTargetException) {
                throw RuntimeException("Failed to invoke constructor '${ReflectionHelper.constructorToString(constructor)}'" +
                        " with args ${accumulator.contentToString()}", e.cause)
            }
        }
    }

    internal class TypeAdapterRuntimeTypeWrapper<T>(
        private val context: Gson,
        private val delegate: TypeAdapter<T>?,
        private val type: Type
    ) : TypeAdapter<T>() {
        @Throws(IOException::class)
        override fun read(`in`: JsonReader): T {
            return delegate!!.read(`in`)
        }

        @Throws(IOException::class)
        override fun write(out: JsonWriter, value: T) {
            var chosen = delegate
            val runtimeType = getRuntimeTypeIfMoreSpecific(
                type, value
            )
            if (runtimeType !== type) {
                @Suppress("UNCHECKED_CAST")
                val runtimeTypeAdapter = context.getAdapter(TypeToken.get(runtimeType)) as TypeAdapter<T>
                chosen = if (runtimeTypeAdapter !is ReflectiveTypeAdapterFactory.Adapter<*, *>) {
                    runtimeTypeAdapter
                } else if (!isReflective(delegate)) {
                    delegate
                } else {
                    runtimeTypeAdapter
                }
            }
            chosen!!.write(out, value)
        }

        private fun isReflective(inputTypeAdapter: TypeAdapter<*>?): Boolean {
            var typeAdapter = inputTypeAdapter
            while (typeAdapter is SerializationDelegatingTypeAdapter<*>) {
                val delegate: TypeAdapter<*> =
                    (typeAdapter as SerializationDelegatingTypeAdapter<*>?)!!.serializationDelegate
                if (delegate === typeAdapter) {
                    break
                }
                typeAdapter = delegate
            }
            return typeAdapter is ReflectiveTypeAdapterFactory.Adapter<*, *>
        }

        private fun getRuntimeTypeIfMoreSpecific(inputType: Type, value: Any?): Type {
            var type = inputType
            if (value != null && (type is Class<*> || type is TypeVariable<*>)) {
                type = value.javaClass
            }
            return type
        }
    }

    companion object {
        private fun <M> checkAccessible(
            `object`: Any?,
            member: M
        ) where M : AccessibleObject?, M : Member? {
            if (!ReflectionAccessFilterHelper.canAccess(
                    member, if (Modifier.isStatic(
                            member!!.modifiers
                        )
                    ) null else `object`
                )
            ) {
                val memberDescription = ReflectionHelper.getAccessibleObjectDescription(member, true)
                throw JsonIOException("$memberDescription is not accessible and ReflectionAccessFilter does not"
                        + " permit making it accessible. Register a TypeAdapter for the declaring type, adjust the"
                        + " access filter or increase the visibility of the element and its declaring type."
                )
            }
        }
    }
}
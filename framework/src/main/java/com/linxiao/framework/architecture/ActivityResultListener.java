package com.linxiao.framework.architecture;

import android.content.Intent;

public interface ActivityResultListener {

    void onResultCallback(int resultCode, Intent data);

}

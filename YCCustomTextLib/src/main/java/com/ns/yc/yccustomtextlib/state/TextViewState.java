/*
Copyright 2017 yangchong211（github.com/yangchong211）

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.ns.yc.yccustomtextlib.state;


import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

/**
 * <pre>
 *     @author yangchong
 *     blog  : https://github.com/yangchong211
 *     time  : 2017/5/18
 *     desc  : 自定义BaseSavedState
 *     revise:
 * </pre>
 */
public class TextViewState extends View.BaseSavedState {

    int mLoadingTime;
    int mLocation;
    boolean isRunning;

    public static final Creator<TextViewState> CREATOR
            = new Creator<TextViewState>() {
        @Override
        public TextViewState createFromParcel(Parcel in) {
            return new TextViewState(in);
        }

        @Override
        public TextViewState[] newArray(int size) {
            return new TextViewState[size];
        }
    };

    TextViewState(Parcelable superState) {
        super(superState);
    }

    TextViewState(Parcel source) {
        super(source);
        mLoadingTime = source.readInt();
        mLocation = source.readInt();
        isRunning = source.readInt() == 1;
    }

    @Override public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(mLoadingTime);
        out.writeInt(mLocation);
        out.writeInt(isRunning ? 1 : 0);
    }
}

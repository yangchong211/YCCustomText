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
package com.ns.yc.yccustomtextlib.edit.state;


import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

/**
 * <pre>
 *     @author yangchong
 *     blog  : https://github.com/yangchong211/YCStatusBar
 *     time  : 2017/5/18
 *     desc  : 自定义BaseSavedState
 *     revise:
 * </pre>
 */
public class TextEditorState extends View.BaseSavedState {

    public int rtImageHeight;
    public String imagePath;

    public static final Creator<TextEditorState> CREATOR
            = new Creator<TextEditorState>() {
        @Override
        public TextEditorState createFromParcel(Parcel in) {
            return new TextEditorState(in);
        }

        @Override
        public TextEditorState[] newArray(int size) {
            return new TextEditorState[size];
        }
    };

    public TextEditorState(Parcelable superState) {
        super(superState);
    }

    public TextEditorState(Parcel source) {
        super(source);
        rtImageHeight = source.readInt();
        imagePath = source.readString();
    }

    @Override public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(rtImageHeight);
        out.writeString(imagePath);
    }
}

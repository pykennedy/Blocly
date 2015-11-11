package com.example.peter.blocly.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.peter.blocly.R;

import java.util.HashMap;
import java.util.Map;

public class RobotoTextView extends TextView {

    private static Map<String, Typeface> sTypefaces = new HashMap<String, Typeface>();

    public RobotoTextView(Context context) {
        super(context);
    }

    public RobotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        extractFont(attrs);
    }

    public RobotoTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        extractFont(attrs);
    }

    void extractFont(AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(
                attrs, R.styleable.Roboto, 0, 0);
        int robotoFontIndex = typedArray.getInteger(R.styleable.Roboto_robotoFont, -1);
        // try something like typedArray.getInteger(R.styleable.Robot_robotoStyle,-1);
        typedArray.recycle();
        String[] stringArray = getResources().getStringArray(R.array.roboto_font_file_names);

        if (robotoFontIndex < 0 || robotoFontIndex >= stringArray.length) {
            return;
        }

        String robotoFont = stringArray[robotoFontIndex];


        Typeface robotoTypeface = null;

        //System.out.println(robotoFont);


        if (sTypefaces.containsKey(robotoFont)) {
            robotoTypeface = sTypefaces.get(robotoFont);
        } else {
            robotoTypeface = Typeface.createFromAsset(getResources().getAssets(),
                    "fonts/RobotoTTF/" + robotoFont);
            sTypefaces.put(robotoFont, robotoTypeface);
        }

        setTypeface(robotoTypeface);

        //System.out.println(robotoFont);
    }
}
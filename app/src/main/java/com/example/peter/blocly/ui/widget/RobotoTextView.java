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
        //int robotoFontIndex = typedArray.getInteger(R.styleable.Roboto_robotoFont, -1);
        boolean condensed = typedArray.getBoolean(R.styleable.Roboto_condensed, false);
        boolean italic = typedArray.getBoolean(R.styleable.Roboto_italic, false);
        int robotoFontIndex = typedArray.getInteger(R.styleable.Roboto_robotoStyle, -1);
        String[] baseFonts = getResources().getStringArray(R.array.robotoStyle);
        if(robotoFontIndex < 0 || robotoFontIndex >= baseFonts.length)
            return;
        String robotoStyle = baseFonts[robotoFontIndex];
        String robotoFontFull = "Roboto-";
        if(condensed) {
            robotoFontFull = "RobotoCondensed-";
        }
        if(robotoStyle.equals("Italic")) {
            robotoFontFull += "Italic";
        }
        else
            robotoFontFull += robotoStyle;
        if(italic && !robotoStyle.equals("Italic"))
            robotoFontFull += "Italic";
        robotoFontFull += ".ttf";

        // try something like typedArray.getInteger(R.styleable.Robot_robotoStyle,-1);
        typedArray.recycle();


        Typeface robotoTypeface = null;

        //System.out.println(robotoFont);


        if (sTypefaces.containsKey(robotoFontFull)) {
            robotoTypeface = sTypefaces.get(robotoFontFull);
        } else {
            robotoTypeface = Typeface.createFromAsset(getResources().getAssets(),
                    "fonts/RobotoTTF/" + robotoFontFull);
            sTypefaces.put(robotoFontFull, robotoTypeface);
        }

        setTypeface(robotoTypeface);

        //System.out.println(robotoFont);
    }
}
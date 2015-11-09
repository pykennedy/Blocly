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
        typedArray.recycle();
        String[] stringArray = getResources().getStringArray(R.array.roboto_font_file_names);

        if (robotoFontIndex < 0 || robotoFontIndex >= stringArray.length) {
            return;
        }

    /* challenge code */
        boolean condensed = false;
        String robotoStyle = "";
        String parseMe = stringArray[robotoFontIndex];
        if(parseMe.contains("Condensed"))
            condensed = true;
        if(parseMe.contains("Black")) robotoStyle += "Black";
        else if(parseMe.contains("Bold")) robotoStyle += "Bold";
        else if(parseMe.contains("Light")) robotoStyle += "Light";
        else if(parseMe.contains("Medium")) robotoStyle += "Medium";
        else if(parseMe.contains("Regular")) robotoStyle += "Regular";
        else if(parseMe.contains("Thin")) robotoStyle += "Thin";
        else robotoStyle += "";
        if(parseMe.contains("Italic")) robotoStyle += "Italic";
        robotoStyle += ".ttf";
    /* challenge code */



        String robotoFont = stringArray[robotoFontIndex];


        Typeface robotoTypeface = null;

        System.out.println(robotoFont);
        System.err.println(robotoStyle);

///* challenge code
        if(condensed) {
            if (sTypefaces.containsKey("RobotoCondensed-"+robotoStyle)) {
                robotoTypeface = sTypefaces.get("RobotoCondensed-"+robotoStyle);
            } else {
                robotoTypeface = Typeface.createFromAsset(getResources().getAssets(),
                        "fonts/RobotoTTF/" + "RobotoCondensed-"+robotoStyle);
                sTypefaces.put("RobotoCondensed-"+robotoStyle, robotoTypeface);
            }
            setTypeface(robotoTypeface);
        }
        else {
            if (sTypefaces.containsKey("Roboto-"+robotoStyle)) {
                robotoTypeface = sTypefaces.get("Roboto-"+robotoStyle);
            } else {
                robotoTypeface = Typeface.createFromAsset(getResources().getAssets(),
                        "fonts/RobotoTTF/" + "Roboto-"+robotoStyle);
                sTypefaces.put("Roboto-"+robotoStyle, robotoTypeface);
            }
            setTypeface(robotoTypeface);
        }
//*/
        /*
                    if (sTypefaces.containsKey(robotoFont)) {
                robotoTypeface = sTypefaces.get(robotoFont);
            } else {
                robotoTypeface = Typeface.createFromAsset(getResources().getAssets(),
                        "fonts/RobotoTTF/" + robotoFont);
                sTypefaces.put(robotoFont, robotoTypeface);
            }

            setTypeface(robotoTypeface);
         */
        System.out.println(robotoFont);
        System.err.println(robotoStyle);
    }
}
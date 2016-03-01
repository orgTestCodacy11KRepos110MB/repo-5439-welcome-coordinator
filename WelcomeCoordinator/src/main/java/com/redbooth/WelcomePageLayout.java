package com.redbooth;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class WelcomePageLayout extends RelativeLayout {
    public WelcomePageLayout(Context context) {
        super(context);
    }

    public WelcomePageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WelcomePageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WelcomePageLayout(Context context, AttributeSet attrs,
                             int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams params) {
        return params instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams params) {
        return new LayoutParams(params);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    List<WelcomePageBehavior> getBehaviors(WelcomeCoordinatorLayout coordinatorLayout) {
        List<WelcomePageBehavior> result = new ArrayList<>();
        for (int index = 0; index < getChildCount(); index++) {
            final View view = getChildAt(index);
            if (view.getLayoutParams() instanceof LayoutParams) {
                final LayoutParams params = (LayoutParams)view.getLayoutParams();
                final WelcomePageBehavior behavior = params.getBehavior();
                if (behavior != null) {
                    behavior.setCoordinator(coordinatorLayout);
                    behavior.setTarget(view);
                    result.add(behavior);
                }
            }
        }
        return result;
    }

    public static class LayoutParams extends RelativeLayout.LayoutParams {
        private WelcomePageBehavior behavior;

        public WelcomePageBehavior getBehavior() {
            return behavior;
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            extractAttributes(context, attrs);
        }

        private void extractAttributes(Context context, AttributeSet attrs) {
            final TypedArray attributes = context.obtainStyledAttributes(attrs,
                    R.styleable.WelcomePageLayout_LayoutParams);
            if (attributes.hasValue(R.styleable.WelcomePageLayout_LayoutParams_view_behavior)) {
                behavior = parseBehavior(context, attrs, attributes
                        .getString(R.styleable.WelcomePageLayout_LayoutParams_view_behavior));
            }
            attributes.recycle();
        }

        private WelcomePageBehavior parseBehavior(Context context, AttributeSet attrs, String name) {
            WelcomePageBehavior result = null;
            if (!TextUtils.isEmpty(name)) {
                final String fullName;
                if (name.startsWith(".")) {
                    fullName = context.getPackageName() + name;
                } else {
                    fullName = name;
                }
                try {
                    Class<WelcomePageBehavior> behaviorClazz
                            = (Class<WelcomePageBehavior>) Class.forName(fullName);
                    final Constructor<WelcomePageBehavior> mainConstructor
                            = behaviorClazz.getConstructor(WelcomePageBehavior.CONSTRUCTOR_PARAMS);
                    mainConstructor.setAccessible(true);
                    result = mainConstructor.newInstance(context, attrs);
                } catch (Exception e) {
                    throw new RuntimeException("Could not inflate Behavior subclass " + fullName, e);
                }
            }
            return result;
        }

        @SuppressWarnings("unused")
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public LayoutParams(RelativeLayout.LayoutParams source) {
            super(source);
        }
    }
}
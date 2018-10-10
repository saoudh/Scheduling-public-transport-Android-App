package de.tudarmstadt.travelreminder.main.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

/**
 * An AutoCompleteTextView, that invokes the autocomplete filter with an delay, to prevent
 * sending autocomplete requests to external sources.
 */
public class DelayAutoCompleteTextView extends android.support.v7.widget.AppCompatAutoCompleteTextView {
    /**
     * Default message id for text cahanges.
     */
    private static final int MESSAGE_TEXT_CHANGED = 100;

    /**
     * Delay in milliseconds.
     */
    private static final int DEFAULT_AUTOCOMPLETE_DELAY = 500;

    /**
     * Handler that runs the filtering method.
     */
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            DelayAutoCompleteTextView.super.performFiltering((CharSequence) msg.obj, msg.arg1);
        }
    };

    /**
     * Progressbar view.
     */
    private ProgressBar progressBar;

    /**
     * Delay of the autocompletion.
     */
    private int delay = DEFAULT_AUTOCOMPLETE_DELAY;

    /**
     * Default constructor.
     *
     * @param context The context where this instance will be shown.
     */
    public DelayAutoCompleteTextView(Context context) {
        this(context, null);
    }

    /**
     * Default constructor.
     *
     * @param context The context where this instance will be shown.
     * @param attrs   Attributes of an xml declaration.
     */
    public DelayAutoCompleteTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.design.R.attr.autoCompleteTextViewStyle);
        this.setAttributes(attrs);
    }

    /**
     * Default constructor.
     *
     * @param context      The context where this instance will be shown.
     * @param attrs        Attributes of an xml declaration.
     * @param defStyleAttr Style definition id.
     */
    public DelayAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Sets the attributes.
     *
     * @param attrs Attributes to set.
     * @attr int app:delay Delay of the autocompletion.
     */
    public void setAttributes(AttributeSet attrs) {
        this.delay = attrs.getAttributeIntValue("app", "delay", DEFAULT_AUTOCOMPLETE_DELAY);
    }

    /**
     * Returns the progressbar view.
     *
     * @return The progressbar view.
     */
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    /**
     * Sets the progressbar view.
     * This view is by default gone and will be visible if the autocompletion starts filtering.
     *
     * @param progressBar The progressbar view.
     */
    public void setProgressBar(ProgressBar progressBar) {
        progressBar.setVisibility(GONE);
        this.progressBar = progressBar;
    }


    // Overwritten methods
    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        handler.removeMessages(MESSAGE_TEXT_CHANGED);
        handler.sendMessageDelayed(
                handler.obtainMessage(MESSAGE_TEXT_CHANGED, text),
                DEFAULT_AUTOCOMPLETE_DELAY
        );
    }

    @Override
    public void onFilterComplete(int count) {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        super.onFilterComplete(count);
    }
}


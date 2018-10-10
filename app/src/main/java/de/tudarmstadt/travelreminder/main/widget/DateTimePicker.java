package de.tudarmstadt.travelreminder.main.widget;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import org.joda.time.DateTime;

import java.util.Calendar;

import de.tudarmstadt.travelreminder.main.model.converter.DateConverter;

/**
 * An simple widget to select a DateTime.
 */
public class DateTimePicker extends android.support.v7.widget.AppCompatEditText {

    /**
     * The date and time dialogs.
     */
    DatePickerDialog dateDialog;
    TimePickerDialog timeDialog;

    /**
     * The current date.
     */
    DateTime dateTime = DateTime.now();

    /**
     * An listener if the DateTime was changed.
     */
    OnDateTimeSetListener listener;

    /**
     * Default Constructor with no attributes.
     * @param context The current context.
     */
    public DateTimePicker(Context context) {
        this(context, null);
    }

    /**
     * Default Constructor with attributes.
     * @param context The current context.
     * @param attrs The attributes to set to this widget.
     */
    public DateTimePicker(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v7.appcompat.R.attr.editTextStyle);
    }

    /**
     * Default Constructor with attributes.
     * @param context The current context.
     * @param attrs The attributes to set to this widget.
     * @param defStyleAttr The style attributes to set to this widget.
     */
    public DateTimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setFocusable(false);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }

    /**
     * Displays the DatePickerDialog.
     */
    private void showDatePicker() {
        if (this.dateDialog == null) {
            this.dateDialog = new DatePickerDialog(
                    this.getContext(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int y, int m, int d) {
                            dateTime = dateTime.withYear(y);
                            dateTime = dateTime.withMonthOfYear(m + 1);
                            dateTime = dateTime.withDayOfMonth(d);
                            DateTimePicker.this.showTimePicker();
                        }
                    },
                    this.dateTime.getYear(),
                    this.dateTime.getMonthOfYear() - 1,
                    this.dateTime.getDayOfMonth()
            );
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        this.dateDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        this.dateDialog.show();
    }

    /**
     * Displays the TomePickerDialog.
     */
    private void showTimePicker() {
        if (this.timeDialog == null) {
            this.timeDialog = new TimePickerDialog(
                    this.getContext(),
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int h, int min) {
                            dateTime = dateTime.withHourOfDay(h);
                            dateTime = dateTime.withMinuteOfHour(min);
                            setText(dateTime);
                            if (listener != null)
                                listener.onDateTimeSet(dateTime);
                        }
                    },
                    this.dateTime.getHourOfDay(),
                    this.dateTime.getMinuteOfHour(),
                    true
            );
        }
        this.timeDialog.show();
    }

    /**
     * Sets the OnDateTimeSetListener.
     * @param listener The listener.
     */
    public void setOnDateTimeSetListener(OnDateTimeSetListener listener) {
        this.listener = listener;
    }

    /**
     * Sets the current DateTime.
     * @param time The new DateTime value.
     */
    public void setDateTime(DateTime time) {
        this.dateTime = time;
        this.setText(time);
    }

    /**
     * Returns the current selected DateTime.
     * @return The current selected DateTime.
     */
    public DateTime getDateTime() {
        return dateTime;
    }

    /**
     * Sets the text to the DateTime.
     * @param time The DateTime to set the text to.
     */
    public void setText(DateTime time) {
        setText(DateConverter.format(
                getContext(),
                time
        ));
    }

    // Interfaces

    /**
     * An interface that defines a OnDateTimeSetListener.
     */
    public interface OnDateTimeSetListener {
        /**
         * Called if the DateTime changes.
         * @param dateTime The new DateTime.
         */
        void onDateTimeSet(DateTime dateTime);
    }
}

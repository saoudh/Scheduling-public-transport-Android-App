package de.tudarmstadt.travelreminder.main.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.Duration;

import de.tudarmstadt.travelreminder.R;
import de.tudarmstadt.travelreminder.main.model.RoutePlanModel;
import de.tudarmstadt.travelreminder.main.model.converter.DateConverter;

/**
 * ViewHolder of the RoutePlanModelListAdapter
 */
abstract class ViewHolder extends RecyclerView.ViewHolder {
    /**
     * Default Type for unknown ViewHolder Types.
     */
    public static final int TYPE_ID = -1;

    /**
     * The View itself.
     */
    private View itemView;

    /**
     * Listener that listens to a click onto the view.
     */
    private OnClickListener clickListener;

    /**
     * Default Constructor.
     * @param itemView The view to render on.
     */
    private ViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) clickListener.onItemClick(ViewHolder.this);
            }
        });
    }

    /**
     * Sets the on click listener.
     * @param listener The listener.
     */
    void setOnClickListener(OnClickListener listener) {
        this.clickListener = listener;
    }

    // Abstracts

    /**
     * Binds a given RoutePlan to the view. So it will set the values of every widget.
     * @param plan The RoutePlanModel to bind to.
     */
    public abstract void onBind(RoutePlanModel plan);

    /**
     * Sets the click listener.
     */
    interface OnClickListener {
        void onItemClick(ViewHolder vh);
    }

    // Implementaions

    /**
     * An ViewHolder that indicates that the RecyclerView is not resolved jet.
     */
    static class NotResolvedViewHolder extends ViewHolder {
        /**
         * The NotResolved type id.
         */
        static final int TYPE_ID = -1;

        /**
         * Default constructor.
         * @param itemView The view itself.
         */
        public NotResolvedViewHolder(View itemView) {
            super(itemView);
        }

        /**
         * Returns the layout id to inflate.
         * @return The layout id.
         */
        static int getLayoutId() {
            return R.layout.home_fragment_not_resolved;
        }

        @Override
        public void onBind(RoutePlanModel plan) {
            // Do Nothing
        }
    }

    /**
     * The ViewHolder that indicates that the RecyclerView is empty.
     */
    static class EmptyViewHolder extends ViewHolder {
        /**
         * The EmptyViewHolder type id.
         */
        static final int TYPE_ID = 0;

        /**
         * Default constructor.
         * @param itemView The view itself.
         */
        EmptyViewHolder(View itemView) {
            super(itemView);
        }

        /**
         * Returns the layout id to inflate.
         * @return The layout id.
         */
        static int getLayoutId() {
            return R.layout.home_fragment_no_plans;
        }

        @Override
        public void onBind(RoutePlanModel plan) {
            // Do Nothing
        }
    }

    /**
     * The ViewHolder of an RoutePlanModel item.
     */
    static class RoutePlanViewHolder extends ViewHolder {
        /**
         * The RoutePlanViewHolder type id.
         */
        static final int TYPE_ID = 1;

        // Saved Views.
        TextView origin;
        TextView destination;
        TextView departureTime;
        TextView delay;
        ImageView mode;
        Context context;

        /**
         * Default constructor.
         * @param itemView The view itself.
         */
        RoutePlanViewHolder(View itemView) {
            super(itemView);
            this.context = itemView.getContext();
            this.origin = (TextView) itemView.findViewById(R.id.home_fragment_plan_list_item_origin);
            this.destination = (TextView) itemView.findViewById(R.id.home_fragment_plan_list_item_destination);
            this.departureTime = (TextView) itemView.findViewById(R.id.home_fragment_plan_list_item_departure_time);
            this.delay = (TextView) itemView.findViewById(R.id.home_fragment_plan_list_item_delay);
            this.mode = (ImageView) itemView.findViewById(R.id.home_fragment_plan_list_item_mode);
        }

        /**
         * Returns the layout id to inflate.
         * @return The layout id.
         */
        static int getLayoutId() {
            return R.layout.home_fragment_plan_list_item;
        }

        @Override
        public void onBind(RoutePlanModel plan) {
            this.origin.setText(plan.getOrigin().getName());
            this.destination.setText(plan.getDestination().getName());
            this.departureTime.setText(
                    DateConverter.format(this.context, plan.getDepartureTime())
            );

            switch (plan.getMode()) {
                case BICYCLING:
                    this.mode.setImageResource(R.drawable.ic_directions_bike_black_24dp);
                    break;
                case TRANSIT:
                    this.mode.setImageResource(R.drawable.ic_directions_transit_black_24dp);
                    break;
                case WALKING:
                    this.mode.setImageResource(R.drawable.ic_directions_walk_black_24dp);
                    break;
                case DRIVING:
                    this.mode.setImageResource(R.drawable.ic_directions_car_black_24dp);
                    break;
                case UNKNOWN:
                default:
                    this.mode.setImageResource(R.drawable.ic_unknown_black_24dp);
                    break;
            }

            if (plan.getDelay().isLongerThan(Duration.ZERO)) {
                this.delay.setText(
                        String.format("+%s", DateConverter.format(plan.getDelay()))
                );
                this.delay.setVisibility(View.VISIBLE);
            } else {
                this.delay.setVisibility(View.GONE);
            }
        }
    }
}

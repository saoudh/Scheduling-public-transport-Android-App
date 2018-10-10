package de.tudarmstadt.travelreminder.main.adapter;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.tudarmstadt.travelreminder.main.RoutePlanViewModel;
import de.tudarmstadt.travelreminder.main.model.RoutePlanModel;

/**
 * A list adapter to add to a RecyclerView and display all stored RoutePlanModels.
 * @param <P> The RoutePlanModel class.
 * @param <VM> The RoutePlanViewModel class.
 */
public class RoutePlanModelListAdapter<P extends RoutePlanModel, VM extends RoutePlanViewModel<P>>
        extends RecyclerView.Adapter<ViewHolder>
        implements RoutePlanViewModel.OnRoutePlanListChangedListener<P> {

    // Instance of the used ViewModel
    private VM viewModel;

    // Listen on clicks on an single item.
    private OnItemClickListener onItemClickListener;

    public RoutePlanModelListAdapter(VM viewModel) {
        this.viewModel = viewModel;
    }

    // Implemented Methods

    /**
     * Sets the <code>OnItemClickListener</code>
     *
     * @param listener The listener.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    /**
     * Returns if the current list is empty.
     *
     * @return True if the current list is empty.
     */
    public boolean isEmpty() {
        return this.viewModel.getRoutePlans().size() == 0;
    }

    /**
     * Returns the item on a certain position.
     *
     * @param position The position to return.
     * @return The item at the given position | null if the position isn't present.
     */
    public P getItem(int position) {
        if (position >= this.viewModel.getRoutePlans().size()) return null;
        return this.viewModel.getRoutePlans().get(position);
    }


    // Overwrites of the RecyclerView.Adapter
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.viewModel.setOnRoutePlanListChangedListener(this);
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        vh.onBind(this.getItem(position));
    }

    @Override
    public int getItemCount() {
        if (this.isEmpty() || !this.viewModel.isResolved()) return 1;
        return this.viewModel.getRoutePlans().size();
    }

    @Override
    public int getItemViewType(int position) {
        if (!this.viewModel.isResolved()) return ViewHolder.NotResolvedViewHolder.TYPE_ID;
        if (this.isEmpty()) return ViewHolder.EmptyViewHolder.TYPE_ID;
        return ViewHolder.RoutePlanViewHolder.TYPE_ID;
    }


    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.viewModel.setOnRoutePlanListChangedListener(null);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        ViewHolder vh;
        switch (viewType) {
            case ViewHolder.RoutePlanViewHolder.TYPE_ID:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(ViewHolder.RoutePlanViewHolder.getLayoutId(), parent, false);
                vh = new ViewHolder.RoutePlanViewHolder(v);
                vh.setOnClickListener(new ViewHolder.OnClickListener() {
                    @Override
                    public void onItemClick(ViewHolder vh) {
                        if (onItemClickListener != null)
                            onItemClickListener.onItemClick(RoutePlanModelListAdapter.this, vh);
                    }
                });
                break;


            case ViewHolder.EmptyViewHolder.TYPE_ID:
                return new ViewHolder.EmptyViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(ViewHolder.EmptyViewHolder.getLayoutId(), parent, false)
                );
            case ViewHolder.NotResolvedViewHolder.TYPE_ID:
            default:
                return new ViewHolder.EmptyViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(ViewHolder.NotResolvedViewHolder.getLayoutId(), parent, false)
                );
        }


        return vh;
    }


    // Listen on changes of the sorted RoutePlan list.
    @Override
    public void onInserted(int position, int count) {
        if (!this.viewModel.isResolved()) return;
        if (position == 0 && count == 1) {
            this.notifyDataSetChanged();
            return;
        }
        this.notifyItemRangeInserted(position, count);
    }

    @Override
    public void onRemoved(int position, int count) {
        if (!this.viewModel.isResolved()) return;
        if (position == 0 && count == 0) {
            this.notifyDataSetChanged();
            return;
        }
        this.notifyItemRangeRemoved(position, count);
    }

    @Override
    public void onMoved(int fromPosition, int toPosition) {
        if (!this.viewModel.isResolved()) return;
        this.notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onChanged(int position, int count) {
        if (!this.viewModel.isResolved()) return;
        this.notifyItemRangeChanged(position, count);
    }

    @Override
    public void onResolved(RoutePlanViewModel<P> viewModel, SortedList<P> plans) {
        this.notifyDataSetChanged();
    }


    // Interfaces

    /**
     * Listening in a click on a single item.
     */
    public interface OnItemClickListener {
        void onItemClick(RecyclerView.Adapter rca, RecyclerView.ViewHolder vh);
    }
}

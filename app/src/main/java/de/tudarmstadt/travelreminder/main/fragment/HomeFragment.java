package de.tudarmstadt.travelreminder.main.fragment;

import android.arch.lifecycle.LifecycleFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.tudarmstadt.travelreminder.R;
import de.tudarmstadt.travelreminder.main.Factory;
import de.tudarmstadt.travelreminder.main.MainActivity;
import de.tudarmstadt.travelreminder.main.RoutePlanViewModel;
import de.tudarmstadt.travelreminder.main.adapter.RoutePlanModelListAdapter;
import de.tudarmstadt.travelreminder.main.model.RoutePlanModel;

public class HomeFragment
        extends LifecycleFragment
        implements RoutePlanModelListAdapter.OnItemClickListener, View.OnClickListener {

    public static final String TAG = "HOME_FRAGMENT";


    Factory factory;

    RoutePlanModelListAdapter adapter;
    RoutePlanViewModel viewModel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        this.factory = (Factory) args.get(MainActivity.ARG_FACTORY);
        this.viewModel = factory.createRoutePlanViewModel(this.getActivity());
        this.adapter = factory.createRoutePlanModelListAdapter(this.viewModel);
        this.adapter.setOnItemClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.setupRecyclerView();
        this.setupFab();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
    }



    public void setupFab() {
        FloatingActionButton fab = (FloatingActionButton) this.getView().findViewById(R.id.home_fragment_fab_add);
        fab.setOnClickListener(this);
    }

    public void setupRecyclerView() {
        RecyclerView list = (RecyclerView) this.getView().findViewById(R.id.home_fragment_plan_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        if (list.getLayoutManager() == null) list.setLayoutManager(layoutManager);
        list.setAdapter(this.adapter);
        list.addItemDecoration(
                new DividerItemDecoration(this.getContext(),
                        DividerItemDecoration.VERTICAL)
        );
        this.setupTouchHandler(list);
    }

    public void setupTouchHandler(RecyclerView rc) {
        (new ItemTouchHelper(new OnSwipeDeleteListener())).attachToRecyclerView(rc);
    }

    // RoutePlanList Item Click
    @Override
    public void onItemClick(RecyclerView.Adapter rca, RecyclerView.ViewHolder vh) {
        RoutePlanModel plan = ((RoutePlanModelListAdapter) rca).getItem(vh.getAdapterPosition());
        Fragment f = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(MainActivity.ARG_FACTORY, this.factory);
        args.putParcelable(DetailFragment.ARG_ROUTE_PLAN, plan);
        f.setArguments(args);
        this.getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_right, R.anim.exit_left, R.anim.enter_left, R.anim.exit_right)
                .addToBackStack(DetailFragment.TAG)
                .replace(R.id.fragment_container, f, DetailFragment.TAG)
                .commit();
    }

    @Override
    public void onClick(View view) {
        NewFragment f = new NewFragment();
        Bundle args = new Bundle();
        args.putParcelable(MainActivity.ARG_FACTORY, factory);
        args.putParcelable(NewFragment.ARG_ROUTE_PLAN, factory.createRoutePlanModel());
        f.setArguments(args);

        this.getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_right, R.anim.exit_left, R.anim.enter_left, R.anim.exit_right)
                .addToBackStack(NewFragment.TAG)
                .replace(R.id.fragment_container, f, NewFragment.TAG)
                .commit();
    }


    private final class OnSwipeDeleteListener extends ItemTouchHelper.SimpleCallback {

        OnSwipeDeleteListener() {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            RoutePlanModelListAdapter adapter = (RoutePlanModelListAdapter) recyclerView.getAdapter();
            if (adapter.isEmpty()) return 0;
            return super.getSwipeDirs(recyclerView, viewHolder);
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder vh, int direction) {
            int position = vh.getAdapterPosition();
            viewModel.deleteRoutePlan(adapter.getItem(position));
        }
    }
}



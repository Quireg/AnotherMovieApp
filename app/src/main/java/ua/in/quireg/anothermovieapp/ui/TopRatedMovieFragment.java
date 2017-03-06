package ua.in.quireg.anothermovieapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.in.quireg.anothermovieapp.R;
import ua.in.quireg.anothermovieapp.adapters.TopRatedMovieRecyclerViewAdapter;
import ua.in.quireg.anothermovieapp.common.Constrains;
import ua.in.quireg.anothermovieapp.core.MovieItem;
import ua.in.quireg.anothermovieapp.interfaces.IMovieListListener;


public class TopRatedMovieFragment extends Fragment {

    private RecyclerView recyclerView;
    private OnListFragmentInteractionListener mListener;
    private IMovieListListener callback;
    private Context mContext;


    public TopRatedMovieFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callback = (IMovieListListener) getContext();
        mContext = getContext();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;

            recyclerView.setLayoutManager(new GridLayoutManager(context, Constrains.COLUMN_NUMBER));
            recyclerView.setAdapter(new TopRatedMovieRecyclerViewAdapter(mListener, Constrains.TOP_RATED));
        }
        reload();
        return view;
    }

    public void reload() {
        System.out.println("Stub");
        if (recyclerView != null && recyclerView.getAdapter() != null) {
            TopRatedMovieRecyclerViewAdapter adapter = (TopRatedMovieRecyclerViewAdapter) recyclerView.getAdapter();
            adapter.addAll(callback.getMoviesList(adapter.getType()));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity != null) {
                ActionBar abar = activity.getSupportActionBar();
                if (abar != null) {
                    abar.setTitle(mContext.getResources().getString(R.string.top_rated_tab_name));
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
        reload();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(MovieItem item);

    }
}

package popularmovies2;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.candidosg.popularmovies.MovieAdapter;
import com.example.candidosg.popularmovies.R;

import java.util.List;

/**
 * Created by candidosg on 23/07/16.
 */
public class MovieVideoAdapter extends ArrayAdapter<MovieVideo> {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieVideoAdapter(Activity context, List<MovieVideo> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
        MovieVideo movie = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie, parent, false);
        }

        return convertView;

    }
}
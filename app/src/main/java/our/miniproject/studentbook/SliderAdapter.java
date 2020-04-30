package our.miniproject.studentbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import de.hdodenhof.circleimageview.CircleImageView;

public class SliderAdapter extends PagerAdapter {

    private Context context;
    private Integer[] images = {R.drawable.slidera, R.drawable.sliderb, R.drawable.sliderc, R.drawable.sliderd};

    public String[] slidertext = {"Available College News", "College Notices", "Improve Learnability", "Store Updates"};

    public SliderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // return super.instantiateItem(container, position);

        View view = layoutInflater.inflate(R.layout.slider_layout, container, false);

        CircleImageView imageView = view.findViewById(R.id.sliderImage);
        TextView textView = (TextView) view.findViewById(R.id.sliderText);

        textView.setText(slidertext[position]);
        imageView.setImageResource(images[position]);

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}

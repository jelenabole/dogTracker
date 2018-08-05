package hr.tvz.trackmydog.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import hr.tvz.trackmydog.R;
import hr.tvz.trackmydog.userModel.BasicDog;

public class GridImageAdapter extends BaseAdapter {
	private Context context;
	private List<Integer> images;
	private int numberOfDogs = 0;
	private List<BasicDog> dogs;

	// TODO - calculate the size of the images:
	private int sizeOfImages = 0;


	Resources res;

	public GridImageAdapter(Context cont, List<BasicDog> listOfDogs) {
		context = cont;
		res = cont.getResources();

		/*
		System.out.println("making an ADAPTER ***");
		System.out.println(cont.getResources().getDisplayMetrics());
        */
		numberOfDogs = listOfDogs.size();
		dogs = listOfDogs;
		images = getPictures();
		for (int i = 0; i < numberOfDogs; i++) {
			if (dogs.get(i) != null) {
				System.out.println(dogs.get(i));
				// TODO - get all dog images - what if dog is 0

			} else {
				numberOfDogs--;
			}
		}
		// images = new ArrayList<>();
	}

	@Override
	public int getCount() {
		return numberOfDogs;
	}

	@Override
	public Object getItem(int position) {
		// TODO - nije position, jer nekad postoji null:
		return dogs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    // padding is the colored space around the image:
		// default sizes:
        int imageBorder = 16;
        int size = 200;

		int spaceBetweenPics = res.getInteger(R.integer.between_pics);
		int widthWithoutSpaces = parent.getWidth() - (spaceBetweenPics * 3);
		size = widthWithoutSpaces / 4;
		parent.setMinimumHeight(size);



        // TODO - provjeriti da li je već izračunata veličina
		// TODO - ili izračunati, ili postaviti tu veličinu slike
		// TODO - ako se računa prvi put, onda promijeniti i veličinu ImageView-a
		if (sizeOfImages == 0) {
			// izračunati veličinu i postaviti istu za imageView
			parent.setBackgroundColor(Color.MAGENTA);
			parent.setMinimumHeight(200);

		}



        // used as space between two images:


        // take whole width and calculate the size:
		// int widthWithoutSpaces = parent.getWidth() - (spaceBetweenPics * (numberOfDogs - 1));
		// TODO - as if there are 4 dogs:


        /*
        System.out.println(spaceBetweenPics);
		System.out.println(numberOfDogs);
         */

		System.out.println(size);

        System.out.println("SIZE MATTERS");
        System.out.println(parent.getWidth());
		System.out.println(parent.getHeight());

        // with simple ImageView
        /*
        System.out.println("position: " + position);
		ImageView imageView = new ImageView(context);
		// TODO - get color by dog.color and set as background

		if (dogs.get(position) != null) {
			System.out.println("dog color: " + dogs.get(position).getColor());
			int color = res.getColor(
					res.getIdentifier(dogs.get(position).getColor(), "color",
							convertView.getContext().getPackageName()),
					convertView.getContext().getTheme());
			imageView.setBackgroundColor(color);
		}
		*/

		// TODO - image with fresco:
		SimpleDraweeView imageView = new SimpleDraweeView(context);


		if (dogs.get(position) != null) {
			if (dogs.get(position).getPhotoURL() ==  null) {
				imageView.setImageResource(images.get(position));
			} else {
				Uri uri = Uri.parse(dogs.get(position).getPhotoURL());
				System.out.println("SLIKA");
				System.out.println(uri);
				imageView.setImageURI(uri);
			}

			// TODO - check if dog has color (or add random):
			int color = res.getColor(
					res.getIdentifier(dogs.get(position).getColor(), "color",
							context.getPackageName()),
					context.getTheme());
			imageView.setBackgroundColor(color);
		}

		imageView.setLayoutParams(new GridView.LayoutParams(size, size));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setPadding(imageBorder, imageBorder, imageBorder, imageBorder);

		return imageView;
	}



	private List<Integer> getPictures() {
		List<Integer> imgList = new ArrayList<>();
		for (int i = 0; i < numberOfDogs; i++) {
			imgList.add(getImgByNumber(i));
		}
		return imgList;
	}

	private int getImgByNumber(int num) {
		switch (num % 5) {
		case 0:
			return R.drawable.dog2;
		case 1:
			return R.drawable.dog1;
		case 2:
			return R.drawable.dog3;
		case 3:
			return R.drawable.dog4;
		default:
			return R.drawable.dog5;
		}
	}

}

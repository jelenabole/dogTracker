package hr.tvz.trackmydog.dogModel;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class CustomDogList extends ArrayAdapter<Dog> {

	private Context context;
	private List<Dog> dogs;

	public CustomDogList(Context context, List<Dog> dogs) {
		super(context, 0, dogs);
		this.context = context;
		this.dogs = dogs;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View listItem = convertView;
		/*
		if(listItem == null)
			listItem = LayoutInflater.from(context).inflate(R.layout.quiz_list_single,parent,false);

		Quiz quiz = quizzes.get(position);
		TextView quizName = (TextView) listItem.findViewById(R.id.quizName);
		TextView professorName = (TextView) listItem.findViewById(R.id.professorName);
		TextView quizTime = (TextView) listItem.findViewById(R.id.quizTime);
		ImageView subjectImage = (ImageView) listItem.findViewById(R.id.subjectImage);

		// TODO - points and button:
		TextView pointsEarned = (TextView) listItem.findViewById(R.id.pointsEarned);
		Button startButton = (Button) listItem.findViewById(R.id.startButton);
		if (quiz.getStartedForCurrentUser()) {
			pointsEarned.setText(quiz.getCurrentUserPoints() + " / " + quiz.getQuiz().getQuestions().size());
			pointsEarned.setVisibility(View.VISIBLE);
			startButton.setVisibility(View.GONE);
		} else {
			pointsEarned.setVisibility(View.GONE);
			startButton.setVisibility(View.VISIBLE);
		}


		quizName.setText(quiz.getQuiz().getName());
		professorName.setText(quiz.getProfessorName());
		if (quiz.getIsSynced()) {
			quizTime.setText("(synced)");
			subjectImage.setImageResource(R.drawable.timer);
		}
		else {
			quizTime.setText(quiz.getOpenUntil());
			subjectImage.setImageResource(R.drawable.deff);
		}
		*/

		// TODO - if started, write the points:



		// image.setImageResource(currentMovie.getmImageDrawable());

		return listItem;
	}

}

package org.homecinecontrol.controlHome;

import java.util.List;

import org.homecinecontrol.R;
import org.homecinecontrol.controlHome.controlElements.ControlElement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ControlElementsAdapter extends ArrayAdapter<ControlElement> {
	private final Context context;
	private final List<ControlElement> values;

	public ControlElementsAdapter(Context context, List<ControlElement> values) {
		super(context, R.layout.listcontrol_row_layout, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ControlElement element = values.get(position);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.listcontrol_row_layout,
				parent, false);
		TextView title = (TextView) rowView
				.findViewById(R.id.listControlRowTitle);
		title.setText(element.getTitle());

		TextView state = (TextView) rowView
				.findViewById(R.id.listControlRowState);
		state.setText(element.getStateString());
		// TextView textView = (TextView) rowView.findViewById(R.id.label);
		// ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		// textView.setText(values[position]);
		// // Change the icon for Windows and iPhone
		// String s = values[position];
		// if (s.startsWith("iPhone")) {
		// imageView.setImageResource(R.drawable.no);
		// } else {
		// imageView.setImageResource(R.drawable.ok);
		// }

		return rowView;
	}
}

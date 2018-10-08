package animation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TimeSliderController extends AbstController
{
	private ArrayList<? extends AbstController> animations;
	private Frame sliderframe;
	private JSlider slider;
	private JLabel timeLabel;
	private boolean stopped = true;
	private int sliderTimeMS;
	private float sliderTimeSec;
	private float delta;
	private float lastTime;
	private float max;

	
	
	public TimeSliderController(ArrayList<? extends AbstController> controllers, int width, int max,
			int step)
	{
		this.max = max;
		this.animations = controllers;
		sliderframe = new Frame("Time Slider");
		sliderframe.setSize(width + 20, 150);
		sliderframe.setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		sliderframe.add(panel);

		// JToggleButton mit Text "Drück mich" wird erstellt
		JButton stopButton = new JButton("Stop");
		JButton runButton = new JButton("Run");
		panel.add(stopButton);
		panel.add(runButton);

		JLabel label = new JLabel("Time: ", JLabel.CENTER);
		panel.add(label);
		timeLabel = new JLabel("", JLabel.CENTER);
		timeLabel.setOpaque(true);
		timeLabel.setBackground(Color.GRAY);
		timeLabel.setForeground(Color.WHITE);
		timeLabel.setPreferredSize(new Dimension(80, 30));
		panel.add(timeLabel);

		// JSlider-Objekt wird erzeugt
		slider = new JSlider();

		slider.setMinimum(0);
		slider.setMaximum(max * 1000);

		// Die Abstände zwischen den
		// Teilmarkierungen werden festgelegt
		slider.setMajorTickSpacing(step * 1000);
		slider.setMinorTickSpacing(step * 1000);
		
		//Create the label table
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		for (int i= 0; i <= max*1000; i+=(step*1000))
		{
			labelTable.put( i, new JLabel(String.valueOf(i/1000f)));
		}
		slider.setLabelTable(labelTable);	
		
		// Zeichnen der Markierungen wird aktiviert
		slider.setPaintTicks(true);
		slider.setPreferredSize(new Dimension(width, 80));

		// Zeichnen der Labels wird aktiviert
		slider.setPaintLabels(true);
		sliderTimeMS = 0;
		sliderTimeSec = 0;
		lastTime = sliderTimeMS;

		// Schiebebalken wird auf den Wert 9 gesetzt
		slider.setValue(sliderTimeMS);

		// Schiebebalken wird dem Panel hinzugefügt
		panel.add(slider);

		sliderframe.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				sliderframe.dispose();
			}
		});

		slider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{			
				sliderTimeMS = slider.getValue();
				sliderTimeSec = sliderTimeMS / 1000f;
				delta = lastTime - sliderTimeSec;
			}
		});

		runButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent e)
			{
				stopped = false;
				delta = lastTime - sliderTimeSec;
			}
		});

		stopButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent e)
			{
				stopped = true;
				sliderTimeMS = slider.getValue();
				sliderTimeSec = sliderTimeMS / 1000f;
			}
		});

		sliderframe.setLocationByPlatform(true);
		sliderframe.setVisible(true);
	}


	/**
	 * Main method to manipulate the channel with the value derived from the 3D
	 * function
	 */
	@Override
	public boolean update(float time)
	{
		lastTime = time;
		if (!stopped)
		{
			sliderTimeSec = time - delta;
			if (sliderTimeSec <= max)
			{
				sliderTimeMS = (int) (sliderTimeSec * 1000f);
			}
			else
			{
				sliderTimeSec = max;
			}
		}
		
		slider.setValue(sliderTimeMS);
		timeLabel.setText(String.format("%4.2f", sliderTimeSec) + " sec");
		
		for (AbstController anim: animations)
			anim.update(sliderTimeSec);

		return false;
	}

}

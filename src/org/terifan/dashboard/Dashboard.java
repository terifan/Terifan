package org.terifan.dashboard;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JPanel;


public class Dashboard extends JPanel
{
	private ArrayList<DashboardComponent> mComponents;
	private DashboardComponent mSelected;


	public Dashboard()
	{
		mComponents = new ArrayList<>();

		super.setBackground(new Color(33,91,125));
		super.setLayout(new DashboardLayoutManager(this));
	}


	@Override
	protected void paintComponent(Graphics aGraphics)
	{
		aGraphics.setColor(getBackground());
		aGraphics.fillRect(0, 0, getWidth(), getHeight());
	}


	public void add(DashboardComponent aCustomizableArea)
	{
		aCustomizableArea.bind(this);

		mComponents.add(aCustomizableArea);

		super.add(aCustomizableArea);
	}


	void moveToTop(DashboardComponent aComponent)
	{
		super.remove(aComponent);
		super.add(aComponent, 0);
	}


	DashboardComponent getSelected()
	{
		return mSelected;
	}


	public void setSelected(DashboardComponent aSelected)
	{
		mSelected = aSelected;
	}
}

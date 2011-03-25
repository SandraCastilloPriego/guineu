/*
 * Copyright 2007-2011 VTT Biotechnology
 * This file is part of Guineu.
 *
 * Guineu is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Guineu is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Guineu; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */


package guineu.modules.statistics.PCA;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

/**
 * @author Taken from MZmine2
 * http://mzmine.sourceforge.net/
 *
 */
public class ProjectionPlotRenderer extends XYLineAndShapeRenderer {

	private Paint[] paintsForGroups;

	private final Color[] avoidColors = { new Color(255, 255, 85)};

	private static final Shape dataPointsShape = new Ellipse2D.Double(-6, -6,
			12, 12);

	private ProjectionPlotDataset dataset;

	private boolean isAvoidColor(Color color) {
		for (Color c : avoidColors) {
			if (	(color.getRed() >= c.getRed()) &&
					(color.getGreen() >= c.getGreen()) &&
					(color.getBlue() >= c.getBlue())	)
				return true;
		}

		return false;
	}

	public ProjectionPlotRenderer(XYPlot plot, ProjectionPlotDataset dataset) {
		super(false, true);
		this.dataset = dataset;
		this.setSeriesShape(0, dataPointsShape);

		paintsForGroups = new Paint[dataset.getNumberOfGroups()];
		DrawingSupplier drawSupp = plot.getDrawingSupplier();
		for (int groupNumber = 0; groupNumber < dataset.getNumberOfGroups(); groupNumber++) {

			Paint nextPaint = drawSupp.getNextPaint();
			while (isAvoidColor((Color) nextPaint))
				nextPaint = drawSupp.getNextPaint();

			paintsForGroups[groupNumber] = nextPaint;

		}

	}

	public Paint getItemPaint(int series, int item) {

		int groupNumber = dataset.getGroupNumber(item);
		return paintsForGroups[groupNumber];
	}

	public Paint getGroupPaint(int groupNumber) {
		return paintsForGroups[groupNumber];
	}

	protected Shape getDataPointsShape() {
		return dataPointsShape;
	}

}

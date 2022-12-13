// Copyright (c) 2020-2023 bransonvitz@protonmail.com All Rights Reserved.
//
// This file is part of jmgpg.
//
// jmgpg is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as
// published by the Free Software Foundation, either version 3 of
// the License, or (at your option) any later version.
//
// jmgpg is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the
// GNU General Public License along with this software.
// If not, see <http://www.gnu.org/licenses>.
package org.apache.jmeter;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.jmeter.config.gui.AbstractConfigGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.engine.event.LoopIterationListener;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;

import org.apache.jmeter.GnuPGPlugin;

public class GnuPGPluginGUI extends AbstractConfigGui {
	private static final Logger LOG = LoggerFactory.getLogger(GnuPGPluginGUI.class);

	private final JTextField ifn = new JTextField();
	private final JTextField ofn = new JTextField();
	private final JTextField ifl = new JTextField();
	private final JTextField ofl = new JTextField();
	private final JCheckBox remove = new JCheckBox("Remove asset(s) after test completion", false);

	public GnuPGPluginGUI() {
		setLayout(new BorderLayout());
		setBorder(makeBorder());
		add(makeTitlePanel(), BorderLayout.NORTH);
		add(createPanel(), BorderLayout.CENTER);
	}

	private JPanel createPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Configure the asset to decrypt"));
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		JLabel ifnLabel = new JLabel("Input file name:");
		JLabel ofnLabel = new JLabel("Output file name:");
		JLabel iflLabel = new JLabel("Input path:");
		JLabel oflLabel = new JLabel("Output path:");
		layout.setHorizontalGroup(
			layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
				.addComponent(ifnLabel)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(ifn))
				.addGroup(layout.createSequentialGroup()
				.addComponent(iflLabel)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(ifl))
				.addGroup(layout.createSequentialGroup()
				.addComponent(ofnLabel)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(ofn))
				.addGroup(layout.createSequentialGroup()
				.addComponent(oflLabel)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(ofl))
				.addGroup(layout.createSequentialGroup()
				.addComponent(remove))); 
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(Alignment.LEADING)
				.addComponent(	ifnLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
									GroupLayout.PREFERRED_SIZE)
				.addComponent(	ifn, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
									GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(Alignment.LEADING)
				.addComponent(	iflLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
									GroupLayout.PREFERRED_SIZE)
				.addComponent(	ifl, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
									GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(Alignment.LEADING)
				.addComponent(	ofnLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
									GroupLayout.PREFERRED_SIZE)
				.addComponent(	ofn, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
									GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(Alignment.LEADING)
				.addComponent(	oflLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
									GroupLayout.PREFERRED_SIZE)
				.addComponent(	ofl, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
									GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(Alignment.LEADING)
				.addComponent(	remove, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
									GroupLayout.PREFERRED_SIZE)));
		return panel;
	}

	@Override
	public String getLabelResource() {
		return "GnuPG";
	}

	@Override
	public String getStaticLabel() {
		return getLabelResource();
	}

	@Override
	public TestElement createTestElement() {
		GnuPGPlugin element = new GnuPGPlugin();
		modifyTestElement(element);
		return element;
	}

	@Override
	public void modifyTestElement(TestElement c) {
		if (c instanceof GnuPGPlugin) {
			GnuPGPlugin element = (GnuPGPlugin)c;
			element.setIFN(ifn.getText());
			element.setIFL(ifl.getText());
			element.setOFN(ofn.getText());
			element.setOFL(ofl.getText());
			element.setRemove(remove.isSelected());
		}
		super.configureTestElement(c);
	}

	@Override
	public void configure(TestElement testelement) {
		super.configure(testelement);
		GnuPGPlugin element = (GnuPGPlugin)testelement;
		ifn.setText(element.getIFN());
		ifl.setText(element.getIFL());
		ofn.setText(element.getOFN());
		ofl.setText(element.getOFL());
		remove.setSelected(element.getRemove());
	}

	@Override
	public void clearGui() {
		super.clearGui();
		ifn.setText("");
		ifl.setText("");
		ofn.setText("");
		ofl.setText("");
		remove.setSelected(false);
	}
}

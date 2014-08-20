/**
 * 
 * @author Klemen
 * 
 * 
 * Main class file for the whole project. UI Thread is located here.
 * 
 * 
 */

package com.muhavision;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import com.muhavision.control.DroneController;
import com.muhavision.cv.image.ImageHelper;
import com.muhavision.cv.image.VisualRenderer;

public class Main {
	
	JFrame controlTowerFrame = new JFrame("Muha Mission Planner");
	
	
	public float roll, pitch, yaw;
	
	VisualRenderer visual = new VisualRenderer(this);
	DroneController controller = new DroneController(visual);
	
	public static final boolean DEBUG = true;
	
	public Main() {
		
		if(!DEBUG) new SplashScreen();
		
		JPanel commands = new JPanel();
		
		commands.setBackground(Color.black);
		
		JButton takeoff = new JButton("Take off");
		takeoff.setBackground(Color.black);
		takeoff.setForeground(Color.white);
		takeoff.setFont(new Font("Arial", Font.PLAIN, 17));
		takeoff.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					controller.getDrone().takeOff();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		takeoff.setFocusable(false);
		commands.add(takeoff);
		
		//main panel
		JPanel visualHolder = new JPanel(new GridLayout());
		visualHolder.add(visual);
		controlTowerFrame.add("Center", visualHolder);
		
		JButton land = new JButton("Land");
		land.setBackground(Color.black);
		land.setForeground(Color.white);
		land.setFont(new Font("Arial", Font.PLAIN, 17));
		land.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controller.getDrone().land();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
		land.setFocusable(false);
		commands.add(land);
		
		controlTowerFrame.add("North", commands);
		
		//controlTowerFrame.setResizable(false);
		//controlTowerFrame.setSize(700, 500);
		
		//controlTowerFrame.setUndecorated(true);	
		controlTowerFrame.setVisible(true);
		controlTowerFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		controlTowerFrame.setFocusable(true);
		controlTowerFrame.setFocusableWindowState(true);
		controlTowerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		controlTowerFrame.addKeyListener(new KeyListener() {	
			@Override public void keyTyped(KeyEvent arg0) {}
			@Override public void keyReleased(KeyEvent arg0) {
				
				if(arg0.getKeyChar()=='w') pitch = 0;
				if(arg0.getKeyChar()=='s') pitch = 0;
				if(arg0.getKeyChar()=='a') roll = 0;
				if(arg0.getKeyChar()=='d') roll = 0;
				
				reloadControls();
			}
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyChar()=='\n')
					try {
						controller.getDrone().sendEmergencySignal();
					} catch (IOException e) {
						e.printStackTrace();
					}
				if(arg0.getKeyCode()==KeyEvent.VK_ESCAPE){ 
					controlTowerFrame.setVisible(false);
					try {
						controller.getDrone().sendEmergencySignal();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if(arg0.getKeyChar()=='w') pitch = -10;
				if(arg0.getKeyChar()=='s') pitch = 10;
				if(arg0.getKeyChar()=='a') roll = -10;
				if(arg0.getKeyChar()=='d') roll = 10;
				
				reloadControls();
				
			}
		});
		
		final Scanner mami_source = new Scanner(System.in);
		
		Thread mami_listener = new Thread(){
			
			public void run(){
				while(true){
					// x;y;z
					String mami_data = mami_source.nextLine();
					
					String[] mami_array = mami_data.split(";");
					
					if(mami_array.length>2){
					
						float mami_pitch = Float.parseFloat(mami_array[1].trim());
						float mami_roll = Float.parseFloat(mami_array[0].trim());
						float mami_yaw = Float.parseFloat(mami_array[2].trim());
						roll = (int)mami_roll;
						pitch = (int)mami_pitch;
						yaw = (int)mami_yaw;
					
						reloadControls();
					
					}
				}
			}
			
		};
		
		mami_listener.start();
		
		controlTowerFrame.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent arg0) {
				/*int x = arg0.getX();
				int w = (int) controlTowerFrame.getSize().getWidth();
				int relative = (w/2) - x;
				if(Math.abs(relative)>50)
					yaw = ((relative - 50)/30)*-1;
				else
					yaw = 0;
				
				reloadControls();*/
			}
			
			@Override public void mouseDragged(MouseEvent arg0) {}
		});
		
		visual.reloadDatas(null, null);
		visual.setDataProps(controlTowerFrame);
	}
	
	protected void reloadControls() {
		//System.out.println(pitch+" : "+roll+" : "+yaw);
		visual.reloadNoData();
		try {
			controller.getDrone().move(roll, pitch, 0, yaw);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public static void main(String[] args) {
		new Main();

	}

}

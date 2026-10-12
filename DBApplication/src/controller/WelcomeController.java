package controller;

import view.ASGui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomeController implements ActionListener {
    private ASGui asgui;

    public WelcomeController (ASGui gui)
    {
        this.asgui = gui;
        gui.setActionListeners(this);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == asgui.getGetStartedButton())
        {
            System.out.println("Get Started Button clicked!");

            asgui.removeGetStartedButton();
            asgui.removeWelcomeImageLabel();

            }
        }
    }

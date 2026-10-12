package controller;

import view.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ReportController implements ActionListener {
    private CreateAdmissionRateDialog createAdmissionRateDialog;
    private CreatePhysicianWorkloadDialog createPhysicianWorkloadDialog;
    private CreateTreatmentStatisticsDialog createTreatmentStatisticsDialog;
    private CreateIllnessOccurrenceDialog createIllnessOccurrenceDialog;

    private ASGui asgui;

    public ReportController(ASGui gui)
    {
        createAdmissionRateDialog = new CreateAdmissionRateDialog(gui);
        createPhysicianWorkloadDialog = new CreatePhysicianWorkloadDialog(gui);
        createTreatmentStatisticsDialog = new CreateTreatmentStatisticsDialog(gui);
        createIllnessOccurrenceDialog = new CreateIllnessOccurrenceDialog(gui);

        this.asgui = gui;
        gui.setActionListeners(this);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == asgui.getARateButton())
        {
            System.out.println("Create Admission Rate Report button clicked!");

            createAdmissionRateDialog.setLocationRelativeTo(asgui); // center it over the main GUI
            createAdmissionRateDialog.setVisible(true);

        }

        if(e.getSource() == asgui.getPHWorkloadButton())
        {
            System.out.println("Create Physician Workload Report button clicked!");

            createPhysicianWorkloadDialog.setLocationRelativeTo(asgui); // center it over the main GUI
            createPhysicianWorkloadDialog.setVisible(true);

        }

        if(e.getSource() == asgui.getTStatisticsButton())
        {
            System.out.println("Create Treatment Statistics Report button clicked!");

            createTreatmentStatisticsDialog.setLocationRelativeTo(asgui); // center it over the main GUI
            createTreatmentStatisticsDialog.setVisible(true);

        }

        if(e.getSource() == asgui.getIOccurrenceButton())
        {
            System.out.println("Create Illness Occurrence Report button clicked!");

            createIllnessOccurrenceDialog.setLocationRelativeTo(asgui); // center it over the main GUI
            createIllnessOccurrenceDialog.setVisible(true);

        }

    }

}

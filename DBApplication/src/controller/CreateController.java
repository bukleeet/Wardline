package controller;

import view.ASGui;
import view.AddPatientDialog;
import view.AddPhysicianDialog;
import view.AddNurseDialog;
import view.AddWardDialog;
import view.AddMedicineDialog;
import view.AddIllnessDialog;

import view.AddAdmissionDialog;
import view.AddDischargeDialog;
import view.AddDiagnosisDialog;
import view.AddNurseAssignmentDialog;
import view.AddTreatmentDialog;

import view.AddPhysicianScheduleDialog;
import view.AddNurseShiftDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateController implements ActionListener {
    private AddPatientDialog addPatientDialog;
    private AddPhysicianDialog addPhysicianDialog;
    private AddNurseDialog addNurseDialog;
    private AddWardDialog addWardDialog;
    private AddMedicineDialog addMedicineDialog;
    private AddIllnessDialog addIllnessDialog;

    private AddAdmissionDialog addAdmissionDialog;
    private AddDischargeDialog addDischargeDialog;
    private AddDiagnosisDialog addDiagnosisDialog;
    private AddNurseAssignmentDialog addNurseAssignmentDialog;
    private AddTreatmentDialog addTreatmentDialog;

    private AddPhysicianScheduleDialog addPhysicianScheduleDialog;
    private AddNurseShiftDialog addNurseShiftDialog;

    private ASGui asgui;
    private int buttonValue;

    public CreateController(ASGui gui)
    {
        addPatientDialog = new AddPatientDialog(gui);
        addPhysicianDialog = new AddPhysicianDialog(gui);
        addNurseDialog = new AddNurseDialog(gui);
        addWardDialog = new AddWardDialog(gui);
        addMedicineDialog = new AddMedicineDialog(gui);
        addIllnessDialog = new AddIllnessDialog(gui);

        addAdmissionDialog = new AddAdmissionDialog(gui);
        addDischargeDialog = new AddDischargeDialog(gui);
        addDiagnosisDialog = new AddDiagnosisDialog(gui);
        addNurseAssignmentDialog = new AddNurseAssignmentDialog(gui);
        addTreatmentDialog = new AddTreatmentDialog(gui);

        addPhysicianScheduleDialog = new AddPhysicianScheduleDialog(gui);
        addNurseShiftDialog = new AddNurseShiftDialog(gui);

        this.asgui = gui;
        gui.setActionListeners(this);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == asgui.getCreateButton())
        {
            System.out.println("Add Patient Button clicked!");
            buttonValue = asgui.getButtonValue();

            switch (buttonValue)
            {
                case 0:
                    addPatientDialog.setLocationRelativeTo(asgui); // center it over the main GUI
                    addPatientDialog.setVisible(true);
                    break;
                case 1:
                    addPhysicianDialog.setLocationRelativeTo(asgui); // center it over the main GUI
                    addPhysicianDialog.setVisible(true);
                    break;
                case 2:
                    addNurseDialog.setLocationRelativeTo(asgui); // center it over the main GUI
                    addNurseDialog.setVisible(true);
                    break;
                case 3:
                    addWardDialog.setLocationRelativeTo(asgui); // center it over the main GUI
                    addWardDialog.setVisible(true);
                    break;
                case 4:
                    addMedicineDialog.setLocationRelativeTo(asgui); // center it over the main GUI
                    addMedicineDialog.setVisible(true);
                    break;
                case 5:
                    addIllnessDialog.setLocationRelativeTo(asgui); // center it over the main GUI
                    addIllnessDialog.setVisible(true);
                    break;
                case 6:
                    addAdmissionDialog.setLocationRelativeTo(asgui); // center it over the main GUI
                    addAdmissionDialog.setVisible(true);
                    break;
                case 7:
                    addDischargeDialog.setLocationRelativeTo(asgui); // center it over the main GUI
                    addDischargeDialog.setVisible(true);
                    break;
                case 8:
                    addDiagnosisDialog.setLocationRelativeTo(asgui); // center it over the main GUI
                    addDiagnosisDialog.setVisible(true);
                    break;
                case 9:
                    addNurseAssignmentDialog.setLocationRelativeTo(asgui); // center it over the main GUI
                    addNurseAssignmentDialog.setVisible(true);
                    break;
                case 10:
                    addTreatmentDialog.setLocationRelativeTo(asgui); // center it over the main GUI
                    addTreatmentDialog.setVisible(true);
                    break;
                case 11:
                    addPhysicianScheduleDialog.setLocationRelativeTo(asgui); // center it over the main GUI
                    addPhysicianScheduleDialog.setVisible(true);
                    break;
                case 12:
                    addNurseShiftDialog.setLocationRelativeTo(asgui); // center it over the main GUI
                    addNurseShiftDialog.setVisible(true);
                    break;
                default:
                    System.out.println("Unknown button value: " + buttonValue);
                    break;
            }
        }
    }
}
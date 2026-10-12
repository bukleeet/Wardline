package view;

import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.event.ActionListener;
import java.util.List;

public class ASGui extends JFrame{

    private Dimension screenSize;
    private int screenWidth;
    private int screenHeight;
    private int mainWidth;
    private int sideWidth;
    private String placeholder = "Search...";
    private int buttonValue;

    private JScrollPane scrollPane;
    private JTabbedPane tabbedPane;

    private JLayeredPane wholeScreen;
    private JLayeredPane mainPanel;
    private JLayeredPane sidePanel;
    private JPanel topPanel;
    private JPanel topPanel2;
    private JPanel tableBGPanel;
    private JPanel listAttributesPanel;

    private JLabel logoLabel;
    private JLabel mainMenuLabel;
    private JLabel transactionsLabel;
    private JLabel recordsLabel;
    private JLabel tableLabel;
    private JLabel profileLabel;
    private JLabel profileNameLabel;
    private JLabel profileJobLabel;
    private JLabel pathLabel;
    private JLabel searchBarLabel;
    private JLabel sortByLabel;
    private JLabel sortByTextLabel;
    private JLabel backgroundLabel;
    private JLabel welcomeImageLabel;

    private JButton getStartedButton;

    private JButton patientButton;
    private JButton physicianButton;
    private JButton nurseButton;
    private JButton wardButton;
    private JButton medicineButton;
    private JButton ailmentButton;

    private JButton admissionButton;
    private JButton dischargeButton;
    private JButton diagnosisButton;
    private JButton nAssignmentButton;
    private JButton treatmentButton;

    private JButton pScheduleButton;
    private JButton nShiftButton;

    private JButton settingsButton;
    private JButton notificationButton;
    private JButton searchButton;
    private JButton cancelButton;
    private JButton dropdownButton;
    private JButton dropdownButton2;
    private JButton filterByButton;
    private JButton createButton;
    private JButton updateButton;
    private JButton deleteButton;

    private JButton aRateButton;
    private JButton phWorkloadButton;
    private JButton tStatisticsButton;
    private JButton iOccurrenceButton;

    private JTextField searchTextField;

	private String lastViewButtonName = "";

    private ImageIcon welcomeImage = new ImageIcon(getClass().getResource("/resources/welcomeImage.png"));
    private ImageIcon backgroundImage = new ImageIcon(getClass().getResource("/resources/backgroundImage.png"));
    private ImageIcon logoImage = new ImageIcon(getClass().getResource("/resources/globeIcon.png"));
    private ImageIcon houseIcon = new ImageIcon(getClass().getResource("/resources/houseIcon.png"));
    private ImageIcon personIcon = new ImageIcon(getClass().getResource("/resources/personIcon.png"));
    private ImageIcon profileIcon = new ImageIcon(getClass().getResource("/resources/profileIcon.png"));
    private ImageIcon settingsIcon = new ImageIcon(getClass().getResource("/resources/settingsIcon.png"));
    private ImageIcon bellIcon = new ImageIcon(getClass().getResource("/resources/bellIcon.png"));
    private ImageIcon dropdownIcon = new ImageIcon(getClass().getResource("/resources/dropdownIcon.png"));
    private ImageIcon dropdownIcon2 = new ImageIcon(getClass().getResource("/resources/dropdownIcon2.png"));
    private ImageIcon searchIcon = new ImageIcon(getClass().getResource("/resources/searchIcon.png"));
    private ImageIcon cancelIcon = new ImageIcon(getClass().getResource("/resources/cancelIcon.png"));

    private List<Component> allTabContents;
    private List<String> allTabTitles;

    JScrollPane patientScrollPane;


    private static Font RobotoRegular;
    private static Font RobotoBold;
    private static Font MontserratBold;

	// ADDED: Getter/Setter for the active view name
	public String getLastViewButtonName() {
        return lastViewButtonName;
    }

	public JLabel getTableLabel()
	{
		return tableLabel;
	}

    public ASGui()
    {
        createFonts();

        // Columns and Data --------------------------------------

//        PatientManagement pm = new PatientManagement();
//        List<Patient> patients = new ArrayList<>();
//        patients = pm.viewPatientRecords();

//        String[] attributes = {" ", "ID", "Patient Name", "Sex", "Birthdate", "Contact Number",
//                "Status"};

//        Object[][] data = {{false, "12648273", new Object[]{profileIcon, "Sunwoo Han"}, "F", "January 01, 1999", "+63 927 636 2540",
//                "Admitted"},
//                {false, "12412600", new Object[]{profileIcon, "Li Zhao Yu"}, "M", "January 01, 1999", "+63 939 838 8404",
//                        "Discharged"},
//                {false, "11428446", new Object[]{profileIcon, "Sabine Callas"}, "F", "January 01, 1999", "+63 927 636 2540",
//                        "Admitted"},
//                {false, "11673941", new Object[]{profileIcon, "Vincent Fabron"}, "M", "January 01, 1999", "+63 939 838 8404",
//                        "Discharged"},
//                {false, "12648273", new Object[]{profileIcon, "Sunwoo Han"}, "F", "January 01, 1999", "+63 927 636 2540",
//                        "Admitted"},
//                {false, "12412600", new Object[]{profileIcon, "Li Zhao Yu"}, "M", "January 01, 1999", "+63 939 838 8404",
//                        "Discharged"},
//                {false, "11428446", new Object[]{profileIcon, "Sabine Callas"}, "F", "January 01, 1999", "+63 927 636 2540",
//                        "Admitted"},
//                {false, "11673941", new Object[]{profileIcon, "Vincent Fabron"}, "M", "January 01, 1999", "+63 939 838 8404",
//                        "Discharged"},
//                {false, "12648273", new Object[]{profileIcon, "Sunwoo Han"}, "F", "January 01, 1999", "+63 927 636 2540",
//                        "Admitted"},
//                {false, "12412600", new Object[]{profileIcon, "Li Zhao Yu"}, "M", "January 01, 1999", "+63 939 838 8404",
//                        "Discharged"}};

//        Object[][] data = new Object[patients.size()][8];
//        for(int i = 0; i < patients.size(); i++)
//        {
//            Patient p = patients.get(i);

//            data[i][0] = false;
//            data[i][1] = p.getPatientID();
//            data[i][2] = new Object[]{ profileIcon, p.getLastName() + ", " + p.getFirstName()};
//            data[i][3] = p.getSex();
//            data[i][4] = p.getBirthDate();
//            data[i][5] = p.getContact();
//            data[i][6] = p.getStatus();
//            data[i][7] = p.getLastName();
//        }
//
//        Map<Integer, Integer> colWidths = Map.of(
//                0, 106,  // checkbox
//                1, 150,  // ID
//                2, 260,  // Name
//                3, 120,  // Sex
//                4, 220,  // Birthdate
//                5, 220,  // Contact
//                6, 150  // Status
//        );

//        JTable table = createTable(data, attributes, 2,    0,    6, colWidths);

        // Scroll Pane -------------------------------------------

        scrollPane = new JScrollPane();

        // JTabbedPane
//        String[] tabs = {"Patients", "Illness", "Treatment", "Physician", "Nurse"};
//
//        JScrollPane[] placeholders = new JScrollPane[tabs.length];
//
//        for (int i = 0; i < placeholders.length; i++)
//        {
//            placeholders[i] = null; // optional, Java initializes to null by default
//        }
//
//        tabbedPane = createTabbedPane(tabs, placeholders);
//        tabbedPane.setBounds(-1, 1, 1230, 785); // Set size here

        String[] tabs = {"Patients", "Patient Related Records", "Illnesses", "Illness Related Records", "Wards", "Physician Schedules",
                "Ward Related Records", "Nurse", "Nurse Related Records", "Physician", "Physician Related Records", "Nurse Shifts",
                "Medicines", "Medicine Related Records", "Diagnosis", "Nurse Assignments", "Admissions", "Treatments", "Discharges"};
        JScrollPane[] placeholders = new JScrollPane[tabs.length];
        for (int i = 0; i < placeholders.length; i++) {
            placeholders[i] = new JScrollPane(); // empty placeholder
        }
        tabbedPane = createTabbedPane(tabs, placeholders);
        tabbedPane.setBounds(-1, 1, 1230, 785);
        allTabContents = new ArrayList<>();
        allTabTitles = new ArrayList<>();
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            allTabContents.add(tabbedPane.getComponentAt(i));
            allTabTitles.add(tabbedPane.getTitleAt(i));
        }


//        tabbedPane.removeAll(); //so that it wont show when you run it initally
//        getScrollPane().add(tabbedPane);



        // Frames ------------------------------------------------

//        screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        screenWidth = 1646;
        screenHeight = 1029;
        mainWidth = 1346;
        sideWidth = 300;

        System.out.println("Screen Width: " + screenWidth + " pixels");
        System.out.println("Screen Height: " + screenHeight + " pixels");

//        this.setTitle("AdmitSys");
//        this.setLayout(null);
//		// MAKE SIZE DYNAMIC
//        this.setSize(1700, 1050);
//		this.setResizable(true);
//		this.setExtendedState(Frame.NORMAL);
//		this.setUndecorated(false);
//        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        this.setVisible(true);

        this.setTitle("AdmitSys");
        this.setLayout(null);
		// MAKE SIZE DYNAMIC
        this.setSize(1780, 1080);
		this.setResizable(true);
		this.setExtendedState(Frame.NORMAL);
		this.setUndecorated(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        this.getContentPane().setBackground(Color.WHITE);


        wholeScreen = new JLayeredPane();
        wholeScreen.setLayout(null);
        wholeScreen.setBounds(0,0, screenWidth, screenHeight);
        wholeScreen.setBackground(new Color(0x749ee2));

        mainPanel = new JLayeredPane();
        mainPanel.setLayout(null);
        mainPanel.setBounds(300,0,mainWidth,screenHeight);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setOpaque(false);

        sidePanel = new JLayeredPane();
        sidePanel.setLayout(null);
        sidePanel.setBounds(0,0,sideWidth,screenHeight);
        sidePanel.setBackground(Color.WHITE);
        sidePanel.setOpaque(true);

        topPanel = new JPanel();
        topPanel.setLayout(null);
        topPanel.setBounds(0,0,mainWidth,64);
        topPanel.setBackground(new Color(0x2e582e));
        topPanel.setOpaque(true);

        topPanel2 = new JPanel();
        topPanel2.setLayout(null);
        topPanel2.setBounds(0,64,mainWidth,50);
        topPanel2.setBackground(Color.WHITE);
        topPanel2.setOpaque(true);

        tableBGPanel = new JPanel();
        tableBGPanel.setLayout(null);
        tableBGPanel.setBounds(60,139,1226,785); //(1346) lr60, t70&b100
        tableBGPanel.setBackground(Color.WHITE);
        tableBGPanel.setOpaque(false);

        listAttributesPanel = new JPanel();
        listAttributesPanel.setLayout(null);
        listAttributesPanel.setBounds(0,0,1226,50);
        listAttributesPanel.setBackground(new Color(0xd5e3d5));
        listAttributesPanel.setOpaque(true);

        // Labels ------------------------------------------------

        logoLabel = new JLabel();
        logoLabel.setIcon(logoImage);
        logoLabel.setBounds(45,0,240,64);
        logoLabel.setText("AdmitSys");
        logoLabel.setFont(MontserratBold.deriveFont(Font.BOLD,28f));
        logoLabel.setVerticalTextPosition(JLabel.CENTER);
        logoLabel.setHorizontalTextPosition(JLabel.RIGHT);
        logoLabel.setForeground(new Color(0x2e582e));
        logoLabel.setBackground(Color.BLUE);

        mainMenuLabel = new JLabel();
        mainMenuLabel.setBounds(50,64,240,50);
        mainMenuLabel.setText("Main Menu");
        mainMenuLabel.setFont(MontserratBold.deriveFont(Font.BOLD,20f));
        mainMenuLabel.setForeground(new Color(0x2e582e));
        mainMenuLabel.setBackground(Color.BLUE);

        transactionsLabel = new JLabel();
        transactionsLabel.setBounds(50,438,240,50);
        transactionsLabel.setText("Transactions");
        transactionsLabel.setFont(MontserratBold.deriveFont(Font.BOLD,20f));
        transactionsLabel.setForeground(new Color(0x2e582e));
        transactionsLabel.setBackground(Color.BLUE);

        recordsLabel = new JLabel();
        recordsLabel.setBounds(50,760,240,50);
        recordsLabel.setText("Schedules");
        recordsLabel.setFont(MontserratBold.deriveFont(Font.BOLD,20f));
        recordsLabel.setForeground(new Color(0x2e582e));
        recordsLabel.setBackground(Color.BLUE);

        tableLabel = new JLabel();
        tableLabel.setBounds(60,0,640,64);
        tableLabel.setText("");
        tableLabel.setFont(MontserratBold.deriveFont(Font.BOLD,28f));
        tableLabel.setForeground(Color.WHITE);
        tableLabel.setBackground(Color.BLUE);

        profileLabel = new JLabel();
        profileLabel.setIcon(profileIcon);
        profileLabel.setBounds(1166,0,180,64);
        profileLabel.setForeground(new Color(0x2e582e));
        profileLabel.setBackground(Color.BLUE);

        profileNameLabel = new JLabel();
        profileNameLabel.setBounds(50,16,120,20);
        profileNameLabel.setText("Ian Lopez");
        profileNameLabel.setFont(RobotoBold.deriveFont(Font.BOLD,14f));
        profileNameLabel.setForeground(Color.WHITE);
        profileNameLabel.setBackground(Color.WHITE);

        profileJobLabel = new JLabel();
        profileJobLabel.setBounds(50,28,120,20);
        profileJobLabel.setText("Physician");
        profileJobLabel.setFont(RobotoBold.deriveFont(Font.BOLD,10f));
        profileJobLabel.setForeground(Color.WHITE);
        profileJobLabel.setBackground(Color.RED);
        // profileJobLabel.setOpaque(true);

        searchBarLabel = new JLabel();
        searchBarLabel.setBounds(736,10,300,30);
        searchBarLabel.setForeground(new Color(0x2e582e));
        searchBarLabel.setBackground(new Color(0xd5e3d5));
        searchBarLabel.setBorder(BorderFactory.createEtchedBorder());
        searchBarLabel.setOpaque(true);

        sortByLabel = new JLabel();
        sortByLabel.setBounds(1046,10,190,30);
        sortByLabel.setForeground(new Color(0x2e582e));
        sortByLabel.setBackground(new Color(0xd5e3d5));
        sortByLabel.setBorder(BorderFactory.createEtchedBorder());
        sortByLabel.setOpaque(true);

        sortByTextLabel = new JLabel();
        sortByTextLabel.setBounds(20,0,140,30);
        sortByTextLabel.setText("Sort By: Name");
        sortByTextLabel.setFont(RobotoBold.deriveFont(Font.BOLD,14f));
        sortByTextLabel.setForeground(new Color(0x2e582e));

        pathLabel = new JLabel();
        pathLabel.setIcon(houseIcon);
        pathLabel.setBounds(60,0,340,50);
        pathLabel.setText("Home   /");
        pathLabel.setFont(RobotoBold.deriveFont(Font.BOLD,20f));
        pathLabel.setVerticalTextPosition(JLabel.CENTER);
        pathLabel.setHorizontalTextPosition(JLabel.RIGHT);
        pathLabel.setIconTextGap(10);
        pathLabel.setForeground(new Color(0x2e582e));
        pathLabel.setBackground(Color.BLUE);
        // pathLabel.setOpaque(true);

        welcomeImageLabel = new JLabel();
        welcomeImageLabel.setIcon(welcomeImage);
        welcomeImageLabel.setBounds(0,0,screenWidth,screenHeight);
        welcomeImageLabel.setBackground(Color.WHITE);
        welcomeImageLabel.setOpaque(true);

        backgroundLabel = new JLabel();
        backgroundLabel.setIcon(backgroundImage);
        backgroundLabel.setBounds(0,114,mainWidth,915);
        backgroundLabel.setBackground(Color.WHITE);
        backgroundLabel.setOpaque(true);

        // Text Field --------------------------------------------

        searchTextField = new JTextField();
        searchTextField.setBounds(50,0,210,30);
        searchTextField.setText(placeholder);
        searchTextField.setFont(RobotoBold.deriveFont(Font.BOLD,14f));
        searchTextField.setBorder(null);
        searchTextField.setForeground(new Color(0x2e582e));
        searchTextField.setBackground(new Color(0xd5e3d5));

        searchTextField.addFocusListener(new java.awt.event.FocusAdapter()
        {
            @Override
            public void focusGained(java.awt.event.FocusEvent e)
            {
                if (searchTextField.getText().equals(placeholder))
                {
                    searchTextField.setText("");
                    searchTextField.setForeground(new Color(0x2e582e)); // normal text color
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e)
            {
                if (searchTextField.getText().isEmpty())
                {
                    searchTextField.setText(placeholder);
                    searchTextField.setForeground(Color.GRAY);
                }
            }
        });

        // Buttons -----------------------------------------------
        buttonValue = 0;

        getStartedButton = new JButton();
        getStartedButton.setBounds(600,720,450,95);
        getStartedButton.setText("Get Started");
        getStartedButton.setFont(MontserratBold.deriveFont(Font.BOLD,40f));
        getStartedButton.setHorizontalAlignment(JLabel.CENTER);
        getStartedButton.setForeground(Color.WHITE);
        getStartedButton.setFocusable(false);
        getStartedButton.setContentAreaFilled(false);   // removes background fill
        getStartedButton.setBorderPainted(false);       // removes border
        getStartedButton.setFocusPainted(false);        // removes focus highlight
        getStartedButton.setOpaque(false);

        patientButton = new JButton();
        patientButton.setBounds(30,126,240,40);
        patientButton.setIcon(personIcon);
        patientButton.setText("Patient");
        patientButton.setFont(RobotoBold.deriveFont(Font.BOLD,20f));
        patientButton.setHorizontalAlignment(JLabel.LEFT);
        patientButton.setIconTextGap(10);
        patientButton.setForeground(new Color(0x2e582e));
        patientButton.setBackground(new Color(0xd5e3d5));
        patientButton.setBorder(BorderFactory.createEtchedBorder());
        patientButton.setFocusable(false);

        physicianButton = new JButton();
        physicianButton.setBounds(30,178,240,40);
        physicianButton.setIcon(personIcon);
        physicianButton.setText("Physician");
        physicianButton.setFont(RobotoBold.deriveFont(Font.BOLD,20f));
        physicianButton.setHorizontalAlignment(JLabel.LEFT);
        physicianButton.setIconTextGap(10);
        physicianButton.setForeground(new Color(0x2e582e));
        physicianButton.setBackground(new Color(0xd5e3d5));
        physicianButton.setBorder(BorderFactory.createEtchedBorder());
        physicianButton.setFocusable(false);

        nurseButton = new JButton();
        nurseButton.setBounds(30,230,240,40);
        nurseButton.setIcon(personIcon);
        nurseButton.setText("Nurse");
        nurseButton.setFont(RobotoBold.deriveFont(Font.BOLD,20f));
        nurseButton.setHorizontalAlignment(JLabel.LEFT);
        nurseButton.setIconTextGap(10);
        nurseButton.setForeground(new Color(0x2e582e));
        nurseButton.setBackground(new Color(0xd5e3d5));
        nurseButton.setBorder(BorderFactory.createEtchedBorder());
        nurseButton.setFocusable(false);

        wardButton = new JButton();
        wardButton.setBounds(30,282,240,40);
        wardButton.setIcon(personIcon);
        wardButton.setText("Ward");
        wardButton.setFont(RobotoBold.deriveFont(Font.BOLD,20f));
        wardButton.setHorizontalAlignment(JLabel.LEFT);
        wardButton.setIconTextGap(10);
        wardButton.setForeground(new Color(0x2e582e));
        wardButton.setBackground(new Color(0xd5e3d5));
        wardButton.setBorder(BorderFactory.createEtchedBorder());
        wardButton.setFocusable(false);

        medicineButton = new JButton();
        medicineButton.setBounds(30,334,240,40);
        medicineButton.setIcon(personIcon);
        medicineButton.setText("Medicine");
        medicineButton.setFont(RobotoBold.deriveFont(Font.BOLD,20f));
        medicineButton.setHorizontalAlignment(JLabel.LEFT);
        medicineButton.setIconTextGap(10);
        medicineButton.setForeground(new Color(0x2e582e));
        medicineButton.setBackground(new Color(0xd5e3d5));
        medicineButton.setBorder(BorderFactory.createEtchedBorder());
        medicineButton.setFocusable(false);

        ailmentButton = new JButton();
        ailmentButton.setBounds(30,386,240,40);
        ailmentButton.setIcon(personIcon);
        ailmentButton.setText("Illness");
        ailmentButton.setFont(RobotoBold.deriveFont(Font.BOLD,20f));
        ailmentButton.setHorizontalAlignment(JLabel.LEFT);
        ailmentButton.setIconTextGap(10);
        ailmentButton.setForeground(new Color(0x2e582e));
        ailmentButton.setBackground(new Color(0xd5e3d5));
        ailmentButton.setBorder(BorderFactory.createEtchedBorder());
        ailmentButton.setFocusable(false);

        admissionButton = new JButton();
        admissionButton.setBounds(30,500,240,40);
        admissionButton.setIcon(personIcon);
        admissionButton.setText("Admission");
        admissionButton.setFont(RobotoBold.deriveFont(Font.BOLD,20f));
        admissionButton.setHorizontalAlignment(JLabel.LEFT);
        admissionButton.setIconTextGap(10);
        admissionButton.setForeground(new Color(0x2e582e));
        admissionButton.setBackground(new Color(0xd5e3d5));
        admissionButton.setBorder(BorderFactory.createEtchedBorder());
        admissionButton.setFocusable(false);

        dischargeButton = new JButton();
        dischargeButton.setBounds(30,552,240,40);
        dischargeButton.setIcon(personIcon);
        dischargeButton.setText("Discharge");
        dischargeButton.setFont(RobotoBold.deriveFont(Font.BOLD,20f));
        dischargeButton.setHorizontalAlignment(JLabel.LEFT);
        dischargeButton.setIconTextGap(10);
        dischargeButton.setForeground(new Color(0x2e582e));
        dischargeButton.setBackground(new Color(0xd5e3d5));
        dischargeButton.setBorder(BorderFactory.createEtchedBorder());
        dischargeButton.setFocusable(false);

        diagnosisButton = new JButton();
        diagnosisButton.setBounds(30,604,240,40);
        diagnosisButton.setIcon(personIcon);
        diagnosisButton.setText("Diagnosis");
        diagnosisButton.setFont(RobotoBold.deriveFont(Font.BOLD,20f));
        diagnosisButton.setHorizontalAlignment(JLabel.LEFT);
        diagnosisButton.setIconTextGap(10);
        diagnosisButton.setForeground(new Color(0x2e582e));
        diagnosisButton.setBackground(new Color(0xd5e3d5));
        diagnosisButton.setBorder(BorderFactory.createEtchedBorder());
        diagnosisButton.setFocusable(false);

        nAssignmentButton = new JButton();
        nAssignmentButton.setBounds(30,708,240,40);
        nAssignmentButton.setIcon(personIcon);
        nAssignmentButton.setText("Nurse Assignment");
        nAssignmentButton.setFont(RobotoBold.deriveFont(Font.BOLD,20f));
        nAssignmentButton.setHorizontalAlignment(JLabel.LEFT);
        nAssignmentButton.setIconTextGap(10);
        nAssignmentButton.setForeground(new Color(0x2e582e));
        nAssignmentButton.setBackground(new Color(0xd5e3d5));
        nAssignmentButton.setBorder(BorderFactory.createEtchedBorder());
        nAssignmentButton.setFocusable(false);

        treatmentButton = new JButton();
        treatmentButton.setBounds(30,656,240,40);
        treatmentButton.setIcon(personIcon);
        treatmentButton.setText("Treatment");
        treatmentButton.setFont(RobotoBold.deriveFont(Font.BOLD,20f));
        treatmentButton.setHorizontalAlignment(JLabel.LEFT);
        treatmentButton.setIconTextGap(10);
        treatmentButton.setForeground(new Color(0x2e582e));
        treatmentButton.setBackground(new Color(0xd5e3d5));
        treatmentButton.setBorder(BorderFactory.createEtchedBorder());
        treatmentButton.setFocusable(false);

        pScheduleButton = new JButton();
        pScheduleButton.setBounds(30,822,240,40);
        pScheduleButton.setIcon(personIcon);
        pScheduleButton.setText("Physician Schedule");
        pScheduleButton.setFont(RobotoBold.deriveFont(Font.BOLD,20f));
        pScheduleButton.setHorizontalAlignment(JLabel.LEFT);
        pScheduleButton.setIconTextGap(10);
        pScheduleButton.setForeground(new Color(0x2e582e));
        pScheduleButton.setBackground(new Color(0xd5e3d5));
        pScheduleButton.setBorder(BorderFactory.createEtchedBorder());
        pScheduleButton.setFocusable(false);

        nShiftButton = new JButton();
        nShiftButton.setBounds(30,874,240,40);
        nShiftButton.setIcon(personIcon);
        nShiftButton.setText("Nurse Shift");
        nShiftButton.setFont(RobotoBold.deriveFont(Font.BOLD,20f));
        nShiftButton.setHorizontalAlignment(JLabel.LEFT);
        nShiftButton.setIconTextGap(10);
        nShiftButton.setForeground(new Color(0x2e582e));
        nShiftButton.setBackground(new Color(0xd5e3d5));
        nShiftButton.setBorder(BorderFactory.createEtchedBorder());
        nShiftButton.setFocusable(false);

        settingsButton = new JButton();
        settingsButton.setBounds(1086,22,20,20);
        settingsButton.setIcon(settingsIcon);
        settingsButton.setContentAreaFilled(false);
        // settingsButton.setBorderPainted(false);
        settingsButton.setFocusable(false);

        notificationButton = new JButton();
        notificationButton.setBounds(1126,22,20,20);
        notificationButton.setIcon(bellIcon);
        notificationButton.setContentAreaFilled(false);
        // notificationButton.setBorderPainted(false);
        notificationButton.setFocusable(false);

        searchButton = new JButton();
        searchButton.setBounds(10,5,20,20);
        searchButton.setIcon(searchIcon);
        searchButton.setContentAreaFilled(false);
        // searchButton.setBorderPainted(false);
        searchButton.setFocusable(false);


        cancelButton = new JButton();
        cancelButton.setBounds(270,5,20,20);
        cancelButton.setIcon(cancelIcon);
        cancelButton.setContentAreaFilled(false);
        // cancelButton.setBorderPainted(false);
        cancelButton.setFocusable(false);

        dropdownButton = new JButton();
        dropdownButton.setBounds(140,20,20,20);
        dropdownButton.setIcon(dropdownIcon);
        dropdownButton.setContentAreaFilled(false);
        // dropdownButton.setBorderPainted(false);
        dropdownButton.setFocusable(false);

        dropdownButton2 = new JButton();
        dropdownButton2.setBounds(160,5,20,20);
        dropdownButton2.setIcon(dropdownIcon2);
        dropdownButton2.setContentAreaFilled(false);
        // dropdownButton2.setBorderPainted(false);
        dropdownButton2.setFocusable(false);

        filterByButton = new JButton();
        filterByButton.setBounds(1246,10,40,30);
        filterByButton.setText("F");
        filterByButton.setFont(RobotoBold.deriveFont(Font.BOLD,18f));
        filterByButton.setHorizontalAlignment(JButton.CENTER);
        filterByButton.setForeground(new Color(0x2e582e));
        filterByButton.setBackground(new Color(0xd5e3d5));
        filterByButton.setBorder(BorderFactory.createEtchedBorder());
        filterByButton.setFocusable(false);
        filterByButton.setOpaque(true);

        createButton = new JButton();
        createButton.setBounds(1046,139,140,40);
        createButton.setText("+ Add Patient");
        createButton.setFont(RobotoBold.deriveFont(Font.BOLD,14f));
        createButton.setHorizontalAlignment(JButton.CENTER);
        createButton.setForeground(Color.WHITE);
        createButton.setBackground(new Color(0x2e582e));
        createButton.setBorder(BorderFactory.createEtchedBorder());
        createButton.setFocusable(false);

        updateButton = new JButton();
        updateButton.setBounds(1196,139,40,40);
        updateButton.setText("U");
        updateButton.setFont(RobotoBold.deriveFont(Font.BOLD,14f));
        updateButton.setHorizontalAlignment(JButton.CENTER);
        updateButton.setForeground(Color.WHITE);
        updateButton.setBackground(new Color(0x2e582e));
        updateButton.setBorder(BorderFactory.createEtchedBorder());
        updateButton.setFocusable(false);

        deleteButton = new JButton();
        deleteButton.setBounds(1246,139,40,40);
        deleteButton.setText("D");
        deleteButton.setFont(RobotoBold.deriveFont(Font.BOLD,14f));
        deleteButton.setHorizontalAlignment(JButton.CENTER);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(0x2e582e));
        deleteButton.setBorder(BorderFactory.createEtchedBorder());
        deleteButton.setFocusable(false);

        aRateButton = new JButton();
        aRateButton.setBounds(170,950,240,40);
        aRateButton.setText("Admission Rates");
        aRateButton.setFont(RobotoBold.deriveFont(Font.BOLD,18f));
        aRateButton.setHorizontalAlignment(JButton.CENTER);
        aRateButton.setForeground(new Color(0x2e582e));
        aRateButton.setBackground(Color.WHITE);
        aRateButton.setBorder(BorderFactory.createEtchedBorder());
        aRateButton.setFocusable(false);

        phWorkloadButton = new JButton();
        phWorkloadButton.setBounds(420,950,240,40);
        phWorkloadButton.setText("Physician Workloads");
        phWorkloadButton.setFont(RobotoBold.deriveFont(Font.BOLD,18f));
        phWorkloadButton.setHorizontalAlignment(JButton.CENTER);
        phWorkloadButton.setForeground(new Color(0x2e582e));
        phWorkloadButton.setBackground(Color.WHITE);
        phWorkloadButton.setBorder(BorderFactory.createEtchedBorder());
        phWorkloadButton.setFocusable(false);

        tStatisticsButton = new JButton();
        tStatisticsButton.setBounds(670,950,240,40);
        tStatisticsButton.setText("Treatment Statistics");
        tStatisticsButton.setFont(RobotoBold.deriveFont(Font.BOLD,18f));
        tStatisticsButton.setHorizontalAlignment(JButton.CENTER);
        tStatisticsButton.setForeground(new Color(0x2e582e));
        tStatisticsButton.setBackground(Color.WHITE);
        tStatisticsButton.setBorder(BorderFactory.createEtchedBorder());
        tStatisticsButton.setFocusable(false);

        iOccurrenceButton = new JButton();
        iOccurrenceButton.setBounds(920,950,240,40);
        iOccurrenceButton.setText("Illness Occurrences");
        iOccurrenceButton.setFont(RobotoBold.deriveFont(Font.BOLD,18f));
        iOccurrenceButton.setHorizontalAlignment(JButton.CENTER);
        iOccurrenceButton.setForeground(new Color(0x2e582e));
        iOccurrenceButton.setBackground(Color.WHITE);
        iOccurrenceButton.setBorder(BorderFactory.createEtchedBorder());
        iOccurrenceButton.setFocusable(false);

        // Layouts -----------------------------------------------

        this.add(wholeScreen);
        wholeScreen.add(getStartedButton);
        wholeScreen.setLayer(getStartedButton, 20);
        wholeScreen.add(welcomeImageLabel);
        wholeScreen.setLayer(welcomeImageLabel, 20);
        wholeScreen.add(sidePanel);
        sidePanel.add(logoLabel);
        sidePanel.add(mainMenuLabel);
        sidePanel.add(patientButton);
        sidePanel.add(physicianButton);
        sidePanel.add(nurseButton);
        sidePanel.add(wardButton);
        sidePanel.add(medicineButton);
        sidePanel.add(ailmentButton);
        sidePanel.add(transactionsLabel);
        sidePanel.add(admissionButton);
        sidePanel.add(dischargeButton);
        sidePanel.add(diagnosisButton);
        sidePanel.add(nAssignmentButton);
        sidePanel.add(treatmentButton);
        sidePanel.add(recordsLabel);
        sidePanel.add(pScheduleButton);
        sidePanel.add(nShiftButton);
        wholeScreen.add(mainPanel);
        mainPanel.add(topPanel);
        topPanel.add(tableLabel);
        topPanel.add(settingsButton);
        topPanel.add(notificationButton);
        topPanel.add(profileLabel);
        profileLabel.add(profileNameLabel);
        profileLabel.add(profileJobLabel);
        profileLabel.add(dropdownButton);
        mainPanel.add(topPanel2);
        topPanel2.add(pathLabel);
        topPanel2.add(searchBarLabel);
        searchBarLabel.add(searchButton);
        searchBarLabel.add(searchTextField);
        searchBarLabel.add(cancelButton);
        topPanel2.add(sortByLabel);
        sortByLabel.add(sortByTextLabel);
        sortByLabel.add(dropdownButton2);
        topPanel2.add(filterByButton);
        mainPanel.add(createButton);
        mainPanel.setLayer(createButton, 12);
        mainPanel.add(updateButton);
        mainPanel.setLayer(updateButton, 12);
        mainPanel.add(deleteButton);
        mainPanel.setLayer(deleteButton, 12);
        mainPanel.add(tableBGPanel);
        mainPanel.setLayer(tableBGPanel, 10);
        tableBGPanel.add(tabbedPane);
        mainPanel.add(aRateButton);
        mainPanel.add(phWorkloadButton);
        mainPanel.add(tStatisticsButton);
        mainPanel.add(iOccurrenceButton);
        mainPanel.add(backgroundLabel);
    }

    public void createFonts() {
        RobotoRegular = loadFont("/resources/RobotoRegular.ttf", 50f);
        RobotoBold = loadFont("/resources/RobotoBold.ttf", 50f);
        MontserratBold = loadFont("/resources/MontserratBold.ttf", 50f);
    }

    private Font loadFont(String path, float size) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) {
                throw new RuntimeException("Font file not found: " + path);
            }

            Font font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);

            return font;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setActionListeners(ActionListener listener)
    {
        getStartedButton.addActionListener(listener);
        patientButton.addActionListener(listener);
        physicianButton.addActionListener(listener);
        nurseButton.addActionListener(listener);
        wardButton.addActionListener(listener);
        medicineButton.addActionListener(listener);
        ailmentButton.addActionListener(listener);
        admissionButton.addActionListener(listener);
        dischargeButton.addActionListener(listener);
        dischargeButton.addActionListener(listener);
        diagnosisButton.addActionListener(listener);
        nAssignmentButton.addActionListener(listener);
        treatmentButton.addActionListener(listener);
        nShiftButton.addActionListener(listener);
        pScheduleButton.addActionListener(listener);
        createButton.addActionListener(listener);
        deleteButton.addActionListener(listener);
        updateButton.addActionListener(listener);
        searchButton.addActionListener(listener);
        aRateButton.addActionListener(listener);
        phWorkloadButton.addActionListener(listener);
        tStatisticsButton.addActionListener(listener);
        iOccurrenceButton.addActionListener(listener);
    }

    public List<Object> getSelectedRowIDs(JTable table) {
        List<Object> selectedIDs = new ArrayList<>();
        if (table == null || table.getModel().getColumnCount() <= 1) {
            return selectedIDs; // Not a valid table or doesn't have an ID column
        }

        for (int i = 0; i < table.getRowCount(); i++) {
            Object checkboxValue = table.getValueAt(i, 0); // Assuming checkbox is always column 0
            if (checkboxValue instanceof Boolean && (Boolean) checkboxValue) {
                // Assuming ID is always in column 1 (index 1)
                selectedIDs.add(table.getValueAt(i, 1));
            }
        }
        return selectedIDs;
    }

    public JButton getCreateButton()
    {
        return createButton;
    }

    public JButton getDeleteButton()
    {
        return deleteButton;
    }

    public JButton getUpdateButton()
    {
        return updateButton;
    }


    public List<Object> getSelectedRowData(JTable table) {
        List<Object> selectedRow = null;
        int selectedCount = 0;

        // This is a defensive check to handle when no table is displayed
        if (table == null || table.getModel() == null) {
            return null;
        }

        for (int i = 0; i < table.getRowCount(); i++) {
            Object checkboxValue = table.getValueAt(i, 0);
            if (checkboxValue instanceof Boolean && (Boolean) checkboxValue) {
                selectedCount++;
                // Store data of the first selected row
                if (selectedCount == 1) {
                    selectedRow = new ArrayList<>();
                    for (int j = 0; j < table.getColumnCount(); j++) {
                        selectedRow.add(table.getValueAt(i, j));
                    }
                }
            }
        }

        if (selectedCount == 1) {
            return selectedRow;
        } else if (selectedCount > 1) {
            JOptionPane.showMessageDialog(this, "Please select only ONE record to update.", "Selection Error", JOptionPane.WARNING_MESSAGE);
        } else {
            // Error: No rows selected. ASGui handles this for getSelectedRowData.
        }
        return null;
    }





    public JTable createTable(
            Object[][] data,
            String[] attributes,
            int iconNameColumnIndex,   // column where value = {ImageIcon, "Name"}, use -1 if none
            int checkboxColumnIndex,   // column containing boolean checkboxes, use -1 if none
            int statusColumnIndex,     // column containing status text for coloring, use -1 if none
            Map<Integer, Integer> columnWidths)
    {
        DefaultTableModel model = new DefaultTableModel(data, attributes)
        {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == checkboxColumnIndex) return Boolean.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == checkboxColumnIndex;
            }
        };

        JTable table = new JTable(model);

        // --- Row height, font, color ---
        table.setFont(RobotoRegular.deriveFont(Font.PLAIN, 14f));
        table.setForeground(new Color(0x2e582e));
        table.setRowHeight(68);

        table.setShowVerticalLines(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getColumnModel().setColumnMargin(0);
        table.setRowSelectionAllowed(false);
        table.setColumnSelectionAllowed(false);
        table.setCellSelectionEnabled(true);

        // Header ------------------------------------------------

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(1226, 49));
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setResizingAllowed(false);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer()
        {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setFont(RobotoBold.deriveFont(Font.BOLD, 14f));
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
                return label;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // Checkbox ----------------------------------------------

        if (checkboxColumnIndex != -1)
        {
            TableColumn selectCol = table.getColumnModel().getColumn(checkboxColumnIndex);
            selectCol.setCellRenderer(new DefaultTableCellRenderer()
            {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                                                               boolean isSelected, boolean hasFocus,
                                                               int row, int column)
                {
                    JCheckBox checkBox = new JCheckBox();
                    if (value instanceof Boolean) checkBox.setSelected((Boolean) value);
                    checkBox.setHorizontalAlignment(SwingConstants.CENTER);
                    checkBox.setBackground(table.getBackground());

                    JPanel panel = new JPanel(new BorderLayout());
                    panel.add(checkBox, BorderLayout.CENTER);
                    panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, table.getGridColor()));
                    return panel;
                }
            });
        }

        /// Name -------------------------------------------------
        if (iconNameColumnIndex != -1)
        {
            TableColumn nameCol = table.getColumnModel().getColumn(iconNameColumnIndex);
            nameCol.setCellRenderer(new DefaultTableCellRenderer()
            {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                                                               boolean isSelected, boolean hasFocus,
                                                               int row, int column)
                {
                    JPanel panel = new JPanel(new BorderLayout());
                    panel.setOpaque(true);
                    panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());

                    if (value instanceof Object[])
                    {
                        Object[] arr = (Object[]) value;
                        ImageIcon profileIcon = (ImageIcon) arr[0];
                        String name = (String) arr[1];

                        JLabel label = new JLabel(name);
                        label.setIcon(profileIcon);
                        label.setIconTextGap(10);
                        label.setFont(RobotoRegular.deriveFont(Font.PLAIN, 14f));
                        label.setForeground(new Color(0x2e582e));
                        label.setHorizontalAlignment(SwingConstants.LEFT);

                        panel.add(label, BorderLayout.CENTER);
                        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, table.getGridColor()));
                    }
                    return panel;
                }
            });
        }

        // Status ------------------------------------------------
        if (statusColumnIndex != -1)
        {
            TableColumn statusCol = table.getColumnModel().getColumn(statusColumnIndex);
            statusCol.setCellRenderer(new DefaultTableCellRenderer()
            {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                                                               boolean isSelected, boolean hasFocus,
                                                               int row, int column)
                {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    if (value != null)
                    {
                        String status = value.toString();
                        if (status.equalsIgnoreCase("Admitted")) {
                            label.setForeground(new Color(0, 128, 0));
                        } else if (status.equalsIgnoreCase("Discharged")) {
                            label.setForeground(Color.RED);
                        } else {
                            label.setForeground(Color.BLACK);
                        }
                    }

                    label.setFont(RobotoRegular.deriveFont(Font.PLAIN, 14f));
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                    return label;
                }
            });
        }
        // Column Width ------------------------------------------

        if (columnWidths != null)
        {
            columnWidths.forEach((index, width) -> table.getColumnModel().getColumn(index).setPreferredWidth(width));
        }

//        scrollPane.setViewportView(table);
//        scrollPane.setBounds(0, 0, 1226, 750);
//
////        tabbedPane.setComponentAt(0, scrollPane);
//        tabbedPane.revalidate();
//        tabbedPane.repaint();

        return table;
    }

    public JTabbedPane createTabbedPane(String[] tabNames, JScrollPane[] tabContents)
    {
        // Aesthetic of Tabbed Pane
        if (tabNames.length != tabContents.length)
        {
            throw new IllegalArgumentException("Tab names and contents must have the same length");
        }

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(RobotoBold.deriveFont(Font.BOLD,14f));
        tabbedPane.setForeground(Color.WHITE);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder());
        tabbedPane.setOpaque(false);

        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI()
        {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                contentBorderInsets = new Insets(0, 0, 0, 0);
                tabAreaInsets = new Insets(0, 0, 0, 0);
            }

            @Override
            protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
                return 40; // Custom tab height
            }

//            @Override
//            protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
//                return 120; // Custom tab width
//            }

            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                              int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(isSelected ? new Color(0x709a70) : new Color(0x2e582e)); // green shades
                g2.fillRect(x, y, w, h);
            }
        });

        // Add Tabs with Placeholder Scroll Panes
        for (int i = 0; i < tabNames.length; i++)
        {
            JScrollPane scrollPane = tabContents[i];
            if (scrollPane == null)
            {
                scrollPane = new JScrollPane(); // create empty placeholder if null
            }

            tabbedPane.addTab(tabNames[i], scrollPane);
        }

        return tabbedPane;
    }

    public void showOnlyTabs(String... visibleTabs) {
        tabbedPane.removeAll();

        for (String name : visibleTabs) {
            int idx = allTabTitles.indexOf(name);
            if (idx != -1) {
                tabbedPane.addTab(name, allTabContents.get(idx));
            }
        }

        tabbedPane.revalidate();
        tabbedPane.repaint();
    }


    public int getTabIndex(String tabName) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).equals(tabName)) {
                return i;
            }
        }
        return -1; // not found
    }

    public JButton getPatientButton()
    {
        return patientButton;
    }

    public JButton getPhysicianButton()
    {
        return physicianButton;
    }

    public JButton getNurseButton()
    {
        return nurseButton;
    }

    public JButton getWardButton()
    {
        return wardButton;
    }

    public JButton getMedicineButton()
    {
        return medicineButton;
    }

    public JButton getAilmentButton()
    {
        return ailmentButton;
    }

    public JButton getAdmissionButton()
    {
        return admissionButton;
    }

    public JButton getDischargeButton()
    {
        return dischargeButton;
    }

    public JButton getDiagnosisButton()
    {
        return diagnosisButton;
    }

    public JButton getnAssignmentButton()
    {
        return nAssignmentButton;
    }

    public JButton getTreatmentButton()
    {
        return treatmentButton;
    }

    public JButton getnShiftButton()
    {
        return nShiftButton;
    }

    public JButton getpScheduleButton()
    {
        return pScheduleButton;
    }

    public JButton getARateButton()
    {
        return aRateButton;
    }

    public JButton getPHWorkloadButton()
    {
        return phWorkloadButton;
    }

    public JButton getTStatisticsButton()
    {
        return tStatisticsButton;
    }

    public JButton getIOccurrenceButton()
    {
        return iOccurrenceButton;
    }

    public ImageIcon getProfileIcon()
    {
        return profileIcon;
    }

    public JPanel getTableBGPanel()
    {
        return tableBGPanel;
    }

    public JScrollPane getScrollPane()
    {
        return scrollPane;
    }

    public void setTableLabel(String tableName)
    {
        tableLabel.setText(tableName);
    }

    public void setPathLabel(String pathString)
    {
        pathLabel.setText(pathString);
    }

    public JTabbedPane getTabbedPane()
    {
        return tabbedPane;
    }


    public JButton getSearchButton()
    {
        return searchButton;
    }

    public JTextField getSearchTextField()
    {
        return searchTextField;
    }

    public void setCreateButtonText(String createText)
    {
        createButton.setText(createText);
    }

    public void setButtonValue(int newValue)
    {
        buttonValue = newValue;
    }

    public int getButtonValue()
    {
        return buttonValue;
    }

    public JScrollPane getPatientScrollPane()
    {
        return patientScrollPane;
    }

    public void setPatientScrollPane(JScrollPane scrollPane)
    {
        patientScrollPane = scrollPane;
    }

    private JTable patientTable;
    public void setCurrentPatientTable(JTable table)
    {
        this.patientTable = table;
    }

    public JTable getCurrentPatientTable() {
        return patientTable;
    }

    JTable physicianTable;
    JScrollPane physicianScrollPane;

    public JTable getPhysicianTable()
    {
        return physicianTable;
    }


    public void setCurrentPhysicianTable(JTable table)
    {
        this.physicianTable = table;
    }

    public void setPhysicianScrollPane(JScrollPane scrollPane)
    {
        physicianScrollPane = scrollPane;
    }

    private JScrollPane physicianSchedulesScrollPane;
    private JTable currentPhysicianSchedulesTable;

    public void setPhysicianSchedulesScrollPane(JScrollPane scrollPane) {
        this.physicianSchedulesScrollPane = scrollPane;
    }

    public void setCurrentPhysicianSchedulesTable(JTable table) {
        this.currentPhysicianSchedulesTable = table;
    }
    public JTable getCurrentPhysicianSchedulesTable() {
        return this.currentPhysicianSchedulesTable;
    }

    JTable nurseTable;
    JScrollPane nurseScrollPane;

    public void setNurseTable(JTable table)
    {
        this.nurseTable = table;
    }

    public void setNurseScrollPane(JScrollPane sp)
    {
        this.nurseScrollPane = sp;
    }

    public JTable getNurseTable()
    {
        return nurseTable;
    }

    JTable nurseShiftTable;
    JScrollPane nurseShiftScrollPane;

    public void setNurseShiftTable(JTable t)
    {
        this.nurseShiftTable = t;
    }

    public void setNurseShiftScrollPane(JScrollPane sp)
    {
        this.nurseShiftScrollPane = sp;
    }

    public JTable getNurseShiftTable()
    {
        return nurseShiftTable;
    }

    JTable treatmentTable;
    JScrollPane treatmentScrollPane;

    public void setTreatmentTable(JTable t)
    {
        treatmentTable = t;
    }

    public void setTreatmentScrollPane(JScrollPane sp)
    {
        treatmentScrollPane = sp;
    }

    public JTable getTreatmentTable()
    {
        return treatmentTable;
    }

    JTable wardTable;
    JScrollPane wardScrollPane;

    public void setWardTable(JTable t)
    {
        wardTable = t;
    }

    public void setWardScrollPane(JScrollPane sp)
    {
        wardScrollPane = sp;
    }

    public JTable getWardTable()
    {
        return wardTable;
    }

    JTable nurseAssignmentTable;
    JScrollPane nurseAssignmentScrollPane;

    public void setNurseAssignmentTable(JTable t)
    {
        nurseAssignmentTable = t;
    }

    public void setNurseAssignmentScrollPane(JScrollPane sp)
    {
        nurseAssignmentScrollPane = sp;
    }

    public JTable getNurseAssignmentTable()
    {
        return nurseAssignmentTable;
    }

    JTable medicineTable;
    JScrollPane medicineScrollPane;

    public void setMedicineTable(JTable t)
    {
        medicineTable = t;
    }

    public void setMedicineScrollPane(JScrollPane sp)
    {
        medicineScrollPane = sp;
    }

    public JTable getMedicineTable()
    {
        return medicineTable;
    }

    JTable illnessTable;
    JScrollPane illnessScrollPane;

    public void setIllnessTable(JTable t)
    {
        illnessTable = t;
    }

    public void setIllnessScrollPane(JScrollPane sp)
    {
        illnessScrollPane = sp;
    }

    public JTable getIllnessTable()
    {
        return illnessTable;
    }

    JTable dischargeTable;
    JScrollPane dischargeScrollPane;

    public void setDischargeTable(JTable t)
    {
        dischargeTable = t;
    }

    public void setDischargeScrollPane(JScrollPane sp)
    {
        dischargeScrollPane = sp;
    }

    public JTable getDischargeTable()
    {
        return dischargeTable;
    }

    JTable admissionTable;
    JScrollPane admissionSrollPane;

    public void setAdmissionTable(JTable t)
    {
        admissionTable = t;
    }

    public void setAdmissionSrollPane(JScrollPane sp)
    {
        admissionSrollPane = sp;
    }

    public JTable getAdmissionTable()
    {
        return admissionTable;
    }

    public JButton getGetStartedButton() { return getStartedButton; }

    public void removeGetStartedButton() {
        wholeScreen.remove(getStartedButton);
        wholeScreen.revalidate();
        wholeScreen.repaint();
    }

    public void removeWelcomeImageLabel() {
        wholeScreen.remove(welcomeImageLabel);
        wholeScreen.revalidate();
        wholeScreen.repaint();
    }

    private JTable diagnosisTable;
    private JScrollPane diagnScrollPane;

    public JTable getDiagnosisTable()
    {
        return diagnosisTable;
    }

    public JScrollPane getDiagnScrollPane()
    {
        return diagnScrollPane;
    }

    public void setDiagnScrollPane(JScrollPane d)
    {
        diagnScrollPane = d;
    }

    public void setDiagnTable(JTable t)
    {
        diagnosisTable = t;
    }







}
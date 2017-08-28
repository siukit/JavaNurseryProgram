import java.awt.*;
import java.util.List;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Nursery extends JFrame implements Serializable{
	//declare the user name and password that are used to login the system
	private static final String username = "admin";
	private static final String password = "pw";
	
	//registrants2 is the arraylist that stores the registrants which will be serialized and deserialized from the harddrive
    private static ArrayList<Registrant> registrants = new ArrayList<Registrant>();   
    //prices is the arraylist that stores the prices which will be serialized and deserialized from the harddrive
    private static ArrayList<Prices> prices = new ArrayList<Prices>();
    //children integer will be used to store the amount of registered children in a selected category
    private static int children;
    //store the date of today in a variable
    private LocalDate today = LocalDate.now();
    //check if the "Prices.dat" file exists (this is the file that stores all the user edited prices)
    private File pricesFile = new File("Prices.dat");
    private boolean exists = pricesFile.exists();
    //create a string array that stores all the sessions data
    private String[] sessionStrings = {"All day (8am to 6pm)","Morning (8am to 12am)", "Lunch (12pm to 1pm)", 
			"Afternoon (1pm to 6pm)", "Pre School (9am to 3.30pm)", "Full Holiday Care (5 years and above)"};
    
    //all the JPanel being used are declared here
	private JTabbedPane tabbedPane = new JTabbedPane();
	private JPanel register = new JPanel();
    private JPanel withdrawChild = new JPanel();
    private JPanel updateInfo = new JPanel();
    private JPanel paymentDetails = new JPanel();
    private JPanel changeSession = new JPanel();
    private JPanel editPrices = new JPanel();
 
    public static void main(String[] args) {
		buildLoginDialog(new Nursery());
	}
	//this is the constructor of the class
	public Nursery(){
		//if the "Prices.dat" file does not exist, three Prices objects will be created which are the default prices for three age groups
	    if(!exists){
	    	prices.add(new Prices(35, 16.5, 5, 18, 25, "Birth to 2 years"));
	    	prices.add(new Prices(34, 15.5, 5, 17, 24.5, "2 to 3 years"));
	    	prices.add(new Prices(32, 14.5, 5, 16, 23.5, "3 years and over"));
	    }
	    //generate monthly invoices (only on appropriate dates)
	    monthlyInvoices();
	    //set the title and size of JFrames 
		setTitle("Nursery program");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		setSize(1000, 700);
		setVisible(true);
		//set it to run on the centre of the screen
		setLocationRelativeTo(null);
		getContentPane().add(tabbedPane);
        
		//add all the JPanels to the tabbedPane and declares names for them
        tabbedPane.add(register,"Register");     
        tabbedPane.add(updateInfo,"Update personal details");
        tabbedPane.add(paymentDetails,"Payment details");
        tabbedPane.add(editPrices,"Edit prices");
        tabbedPane.add(changeSession,"Update session info");
        tabbedPane.add(withdrawChild,"Withdraw child");

        
        //run all the methods that build JPanels
        buildRegisterPanel();
        buildUpdatePanel();
        buildWithdrawPanel();
        buildPricesPanel();
        changeSessionPanel();
        paymentsPanel();
	}
	
	//referenced from http://stackoverflow.com/questions/7323086/java-swing-how-can-i-implement-a-login-screen-before-showing-a-jframe
	//this method set up a login dialog where user needs to enter username and password before they can access the system
	private static void buildLoginDialog(JFrame nursery) {
		//set the nursery program to invisible by default
		nursery.setVisible(false);
		JDialog loginDialog = new JDialog();
		JLabel userNameLabel = new JLabel("Username");
		JLabel passwordLabel = new JLabel("Password");
		JTextField userNameField = new JTextField(15);
		JPasswordField passwordField = new JPasswordField(15);
		JButton loginButton = new JButton("Login");
		JLabel status = new JLabel(" ");
		JButton cancelButton = new JButton("Cancel");
		status.setForeground(Color.RED);
		loginDialog.getRootPane().setDefaultButton(loginButton);
		
		loginDialog.setLayout(new GridBagLayout());	
		GridBagConstraints gc = new GridBagConstraints();
		
		 //increase the margin between components
        gc.insets = new Insets(5,5,5,5);
        
        gc.anchor = GridBagConstraints.LINE_START;
        gc.gridx = 0;
        gc.gridy = 0;
        loginDialog.add(userNameLabel, gc);
        
        gc.gridx = 1;
        gc.gridy = 0;
        loginDialog.add(userNameField, gc);
        
        gc.anchor = GridBagConstraints.LINE_START;
        gc.gridx = 0;
        gc.gridy = 1;
        loginDialog.add(passwordLabel, gc);
        
        gc.gridx = 1;
        gc.gridy = 1;
        loginDialog.add(passwordField, gc);
        
        gc.gridx = 1;
        gc.gridy = 2;
        loginDialog.add(status, gc);
        
        gc.gridx = 1;
        gc.gridy = 3;
        loginDialog.add(loginButton, gc);
        
        gc.anchor = GridBagConstraints.LINE_END;
        gc.gridx = 1;
        gc.gridy = 3;
        loginDialog.add(cancelButton, gc);
        
        loginDialog.setVisible(true);
        loginDialog.pack();
        loginDialog.setLocationRelativeTo(null);
        loginDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        //if the user put in the right username and password, the nursery program will appear for use
        loginButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if (Arrays.equals(password.toCharArray(), passwordField.getPassword())
                        && username.equals(userNameField.getText())) {
                    nursery.setVisible(true);
                    loginDialog.setVisible(false);
                } else {
                	//if the username or password is wrong, the following message appears in the dialog
                	status.setText("Invalid username or password");
                }
        	}
        });
        //cancel button is used to exit the dialog
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nursery.setVisible(false);
                loginDialog.dispose();
                System.exit(0);
            }
        });
	}
	
	 //deserialize the arraylist from Registrant.dat to "registrants", referenced from http://stackoverflow.com/questions/22323959/save-changes-permanently-in-an-arraylist
	private void deserializeRegistrants(){
	    try {
		        FileInputStream fis = new FileInputStream("Registrants.dat");
		        ObjectInputStream ois = new ObjectInputStream(fis);
		        registrants = (ArrayList<Registrant>) ois.readObject();
		        ois.close();
		    } catch(Exception e) {
		        e.printStackTrace();
		    }
	}
	
	// serialize registrants arraylist back to the file, referenced from http://stackoverflow.com/questions/22323959/save-changes-permanently-in-an-arraylist
	private void serializeRegistrants(){
		try {
	            FileOutputStream fos = new FileOutputStream("Registrants.dat");
	            ObjectOutputStream oos = new ObjectOutputStream(fos);
	            oos.writeObject(registrants);
	            oos.close();
        	} catch(Exception e) {
        		e.printStackTrace();
        	}
	}
	
	//deserialize the arraylist from Prices.dat to "prices"
	private void deserializePrices(){
		try {
	        FileInputStream fis = new FileInputStream("Prices.dat");
	        ObjectInputStream ois = new ObjectInputStream(fis);
	        prices = (ArrayList<Prices>) ois.readObject();
	        ois.close();
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	}
		
	// serialize prices arraylist back to the file
	private void serializePrices(){
		try {
            FileOutputStream fos = new FileOutputStream("Prices.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(prices);
            oos.close();
    	} catch(Exception e) {
    		e.printStackTrace();
    	}	
	}
	
	private void monthlyInvoices(){
		deserializeRegistrants();
		for (Registrant reg : registrants) {
			//if it is the first time system generate invoice, the invoice day would be the day when registrant registered
			//if the person has paid the last invoice, the new invoice date will change to the next invoice day
			if(reg.isPaid() && today.isEqual(reg.getNextInvoiceDate())){
				reg.setInvoiceDate(reg.getNextInvoiceDate());
				reg.setPaid(false);
			}
			//get the days between current invoice day to next invoice day to calculate the charge for the child that month
			long daysBetween = ChronoUnit.DAYS.between(reg.getInvoiceDate(), reg.getNextInvoiceDate());
			//if it is after the person's payment due date and payment haven't received yet, the fee will be increased by ten percent
			if(today.isEqual(reg.getPaymentDueDate().plusDays(1)) && !reg.isPaid()){
				reg.setFee(generateCharge(reg.getSession(), reg.getAgeGroup()) * daysBetween + (generateCharge(reg.getSession(), reg.getAgeGroup()) * daysBetween)/10);
			}
			
			//reset the next invoice day
			reg.setNextInvoiceDate(reg.getInvoiceDate().plusMonths(1));
			
			
			//need to update the child age group every time before the monthly invoice being generated
			reg.setAgeGroup(calculateAgeGroup(reg.getBirthDate()));
			reg.setFee(generateCharge(reg.getSession(), reg.getAgeGroup()) * daysBetween);
			reg.setPaymentDueDate(reg.getInvoiceDate().plusDays(7));
			
			//create a formatter to format the decimals properly
    		NumberFormat formatter = new DecimalFormat("#0.00");         		
    		//write the invoice to a txt file named as the child's name
    		PrintWriter invoice;
    		//Invoice will be generated if today is the invoice day of that registrant
    		if(today.isEqual(reg.getInvoiceDate())){
				try {
					invoice = new PrintWriter("Monthly Invoices/" + reg.getChildName() + ".txt");
					invoice.println("Child's name: " + reg.getChildName());
					invoice.println("Carer's name: " + reg.getCarerName());
					invoice.println("Child's date of birth: " + reg.getChildName());
					invoice.println("Contact number: " + reg.getPhoneNumber());
					invoice.println("Address: " + reg.getAddress());
					invoice.println("Selected session: " + sessionStrings[reg.getSession()]);
					//get the registration fee (deposit and 25 pound fee)using the generateCharge method 
					//which returns the child's daily charge and times 7 which is the weekly fee and plus 25
					invoice.println("Fee for this month: £" + formatter.format(reg.getFee()));
					invoice.close();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
    		}
 
		}
		serializeRegistrants();
	}
	
	private void paymentsPanel(){
		deserializeRegistrants();
		class PaymentsTableModel extends AbstractTableModel{
			String[] columnNames = {"Child's name", "Invoice date", "Payment due", "Paid"};
			
				@Override
			    public int getColumnCount()
			    {
			        return columnNames.length;
			    }
			 
			    @Override
			    public String getColumnName(int column)
			    {
			        return columnNames[column];
			    }
			    
			    @Override
			    public int getRowCount()
			    {
			    	//the amount of row is the same as the amount of prices object
			        return registrants.size();
			    }
			    
			    @Override
			    public Class getColumnClass(int column)
			    {
			        switch (column)
			        {
			        	case 0: return String.class;
			        	case 1: return String.class;
			        	case 2: return String.class;
			        	case 3: return Boolean.class;
			            default: return String.class;
			        }
			    }
			     
			    @Override
			    public boolean isCellEditable(int row, int column)
			    {
			        switch (column)
			        {	
			        	//only the third column (boolean) is editable
			        	case 3: return true;
			            default: return false;
			        }
			    }
			     
			    @Override
			    public Object getValueAt(int row, int column)
			    {
			        Registrant registrant = getRegistrant(row);
			     
			        switch (column)
			        {
			    
			            case 0: return registrant.getChildName();
			            case 1: return registrant.getInvoiceDate();
			            case 2: return registrant.getPaymentDueDate();
			            case 3: return registrant.isPaid();			            
			            default: return null;
			        }
			        
			    }
			     
			    @Override
			    public void setValueAt(Object value, int row, int column)
			    {
			        Registrant registrant = getRegistrant(row);
			     
			        switch (column)
			        {
			        	
			            case 3: registrant.setPaid((Boolean)value); break;
			            
			        }
			        fireTableCellUpdated(row, column);
			        //serialize the changed registrants arraylist back to file
			        serializeRegistrants();		        
			    }
			     
			    public Registrant getRegistrant(int row)
			    {
			        return registrants.get( row );
			    }

	
		    }
		    
		paymentDetails.setLayout(new BorderLayout());
		//declare the table that uses the custom table model
		JTable table2 = new JTable(new PaymentsTableModel());
		JScrollPane scrollPane2 = new JScrollPane(table2);
		table2.setRowHeight(25);
		//add the scroll pane which contains the table to the JPanel
		paymentDetails.add(scrollPane2, BorderLayout.CENTER);
		paymentDetails.setVisible(true);
	}
	
	private void changeSessionPanel(){
		deserializeRegistrants();
		deserializePrices();
		
		//create JLabels and JTextFields that ask user to enter child's name and birthday.
		JLabel nameLabel = new JLabel("Enter the child's name:");
		JTextField nameField = new JTextField(30);
		JLabel dobLabel = new JLabel("Enter the child's birthday (DD/MM/YYYY):");
		JTextField dobField = new JTextField(15);
		//create a jcombobox that contains the list of sessions
		JLabel sessionLabel = new JLabel("Choose the new session for this child:");
		JComboBox sessionList = new JComboBox(sessionStrings);
		//create a jbutton that will trigger a action listener
		JButton changeSessionButton = new JButton("Update session");
		
		//add a actionlistener to the button that will change the child's session index
		changeSessionButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		//ask the user to confirm if he/she wants to withdraw that child
        		int selectedOption = JOptionPane.showConfirmDialog(null, 
                        "Are you sure you want to update the session info for this child?", 
                        "Please confirm", 
                        JOptionPane.YES_NO_OPTION); 
        		//if the the user clicked yes
        		if (selectedOption == JOptionPane.YES_OPTION) {
        			//"test" is used to test if the user entered the wrong data
        			boolean test = false;
        			for (Registrant reg : registrants) {
        				//if the registrant object matches the data that user inputed, that child's session index
                        if(reg.getChildName().equals(nameField.getText()) && reg.getBirthDate().equals(dobField.getText())){
                        	reg.setSession(sessionList.getSelectedIndex()); 
                        	serializeRegistrants();
                          	test = true;
                        }  
                    }
        			//if the system couldn't find the child data then a warning message would appear
        			if(!test){
        				JOptionPane.showMessageDialog(null,
                    		    "The child do not exist on the record, please try again.",
                    		    "Warning",
                    		    JOptionPane.ERROR_MESSAGE);
        			}
        		}
        	}
		});
		//put all the JLabels and testFields on the desired places using Gridbaglayout
		changeSession.setLayout(new GridBagLayout());	
		GridBagConstraints gc = new GridBagConstraints();
		
        //increase the margin between components
        gc.insets = new Insets(5,5,5,5);
        
        gc.anchor = GridBagConstraints.LINE_START;
        gc.gridx = 0;
        gc.gridy = 0;
        changeSession.add(nameLabel, gc);
        
        gc.gridx = 1;
        gc.gridy = 0;
        changeSession.add(nameField, gc);
        
        gc.anchor = GridBagConstraints.LINE_START;
        gc.gridx = 0;
        gc.gridy = 1;
        changeSession.add(dobLabel, gc);
        
        gc.gridx = 1;
        gc.gridy = 1;
        changeSession.add(dobField, gc);
        
        gc.anchor = GridBagConstraints.LINE_START;
        gc.gridx = 0;
        gc.gridy = 2;
        changeSession.add(sessionLabel, gc);
        
        gc.gridx = 1;
        gc.gridy = 2;
        changeSession.add(sessionList, gc);
        
        gc.gridx = 1;
        gc.gridy = 3;
        changeSession.add(changeSessionButton, gc);
        //set the JPanel to visible
        changeSession.setVisible(true);
	}
	
	//this method build the panel that allows editing the prices for different sessions and ages
	private void buildPricesPanel(){
		//call the method that deserialize data from file to the arraylist prices
		deserializePrices();
		//create a custom table model in order to manualy put variables into the cells
		class PricesTableModel extends AbstractTableModel{
			String[] columnNames = {"Age group", "All day (8am to 6pm)", "Morning (8am to 12am)", "Lunch (12pm to 1pm)", 
					"Afternoon (1pm to 6pm)", "Pre School (9am to 3.30pm)"};
			
				@Override
			    public int getColumnCount()
			    {
			        return columnNames.length;
			    }
			 
			    @Override
			    public String getColumnName(int column)
			    {
			        return columnNames[column];
			    }
			    
			    @Override
			    public int getRowCount()
			    {
			    	//the amount of row is the same as the amount of prices object
			        return prices.size();
			    }
			    
			    @Override
			    public Class getColumnClass(int column)
			    {
			        switch (column)
			        {
			        	case 0: return String.class;
			        	case 1: return Double.class;
			        	case 2: return Double.class;
			        	case 3: return Double.class;
			        	case 4: return Double.class;
			        	case 5: return Double.class;
			        	case 6: return Double.class;
			            default: return String.class;
			        }
			    }
			     
			    @Override
			    public boolean isCellEditable(int row, int column)
			    {
			        switch (column)
			        {	
			        	//all cells are editable
			            default: return true;
			        }
			    }
			     
			    @Override
			    public Object getValueAt(int row, int column)
			    {
			        Prices price = getPrice(row);
			     
			        switch (column)
			        {
			        	//put the values of each objects on a row, and store each value in a column
			            case 0: return price.getAgeGroup();
			            case 1: return price.getAllDay();
			            case 2: return price.getMorning();
			            case 3: return price.getLunch();
			            case 4: return price.getAfternoon();
			            case 5: return price.getPreSchool();
			            default: return null;
			        }
			    }
			     
			    @Override
			    public void setValueAt(Object value, int row, int column)
			    {
			    	Prices price = getPrice(row);
			     
			        switch (column)
			        {
			        	//allows user to edit the prices variables
			        	case 0: price.setAgeGroup((String)value); break;
			            case 1: price.setAllDay((Double)value); break;
			            case 2: price.setMorning((Double)value); break;
			            case 3: price.setLunch((Double)value); break;
			            case 4: price.setAfternoon((Double)value); break;
			            case 5: price.setPreSchool((Double)value); break;
      
			        }
			        fireTableCellUpdated(row, column);
			        //serialize all the changed objects of the prices arraylist and serialize it to the file
			        serializePrices();
			        
			    }
			     
			    public Prices getPrice(int row)
			    {
			        return prices.get( row );
			    }
	
		    }
		    
		editPrices.setLayout(new BorderLayout());
		//declare the table that uses the custom table model
		JTable table2 = new JTable(new PricesTableModel());
		JScrollPane scrollPane2 = new JScrollPane(table2);
		table2.setRowHeight(25);
		//add the scroll pane which contains the table to the JPanel
		editPrices.add(scrollPane2, BorderLayout.CENTER);
		editPrices.setVisible(true);
	}
	
	//this JPanel allows user to withdraw child from the record
	private void buildWithdrawPanel(){
		//deserialize the registrants arraylist from file
		deserializeRegistrants();		
		//create JLabels and JTextFields that ask user to enter child's name and birthday.
		JLabel nameLabel = new JLabel("Enter the child's name:");
		JTextField nameField = new JTextField(30);
		JLabel dobLabel = new JLabel("Enter the child's birthday (DD/MM/YYYY):");
		JTextField dobField = new JTextField(15);
		JButton withdrawButton = new JButton("Withdraw this child");
		
		//create a action listener that will be triggered when the withdrawButton being clicked
		withdrawButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		//ask the user to confirm if he/she wants to withdraw that child
        		int selectedOption = JOptionPane.showConfirmDialog(null, 
                        "Are you sure you want to withdraw this child from the record?", 
                        "Please confirm", 
                        JOptionPane.YES_NO_OPTION); 
        		//if the the user clicked yes
        		if (selectedOption == JOptionPane.YES_OPTION) {
        			//"test" is used to test if the user entered the wrong data
        			boolean test = false;
        			for (Registrant reg : registrants) {
        				//if the registrant object matches the data that user inputed, that registrant object and the invoice will be removed 
                        if(reg.getChildName().equals(nameField.getText()) && reg.getBirthDate().equals(dobField.getText())){
                        	registrants.remove(reg);
                        	serializeRegistrants();
                        	File file = new File("Invoices/" + reg.getChildName() + ".txt");
                        	file.delete();
                        	test = true;
                        }  
                    }
        			//if the system couldn't find the child data then a warning message would appear
        			if(!test){
        				JOptionPane.showMessageDialog(null,
                    		    "The child do not exist on the record, please try again.",
                    		    "Warning",
                    		    JOptionPane.ERROR_MESSAGE);
        			}
        		}
        	}
		});
		
		//put all the JLabels and testFields on the desired places using Gridbaglayout
		withdrawChild.setLayout(new GridBagLayout());	
		GridBagConstraints gc = new GridBagConstraints();
		
        //increase the margin between components
        gc.insets = new Insets(5,5,5,5);
        
        gc.anchor = GridBagConstraints.LINE_START;
        gc.gridx = 0;
        gc.gridy = 0;
        withdrawChild.add(nameLabel, gc);
        
        gc.gridx = 1;
        gc.gridy = 0;
        withdrawChild.add(nameField, gc);
        
        gc.anchor = GridBagConstraints.LINE_START;
        gc.gridx = 0;
        gc.gridy = 1;
        withdrawChild.add(dobLabel, gc);
        
        gc.gridx = 1;
        gc.gridy = 1;
        withdrawChild.add(dobField, gc);
        
        gc.gridx = 1;
        gc.gridy = 2;
        withdrawChild.add(withdrawButton, gc);
        //set the JPanel to visible
		withdrawChild.setVisible(true);
	}
	
	//this JPanel allows user to update registrants data
	private void buildUpdatePanel(){
		
		deserializeRegistrants();
		//create custom model in order to manually put registrants objects data into the table
		class RegistrantTableModel extends AbstractTableModel{
			//declare the column names
			String[] columnNames = {"Child's name", "Carer's name", "Child's birthday", "Address", "Allergy Info", "Contact number", "Required session"};

			 @Override
		    public int getColumnCount()
		    {
		        return columnNames.length;
		    }
		 
		    @Override
		    public String getColumnName(int column)
		    {
		        return columnNames[column];
		    }
		 
		    @Override
		    public int getRowCount()
		    {
		        return registrants.size();
		    }
		    
		    @Override
		    public Class getColumnClass(int column)
		    {
		        switch (column)
		        {
		            
		            default: return String.class;
		        }
		    }
		     
		    @Override
		    public boolean isCellEditable(int row, int column)
		    {
		        switch (column)
		        {	
		        	//the "Required session" columns are not editable
		        	case 6: return false;
		            default: return true;
		        }
		    }
		     
		    @Override
		    public Object getValueAt(int row, int column)
		    {
		        Registrant registrant = getRegistrant(row);
		     
		        switch (column)
		        {
		        	/*note that the sixth columns takes the integer of session's index from the object and convert them 
		        	 * to the session strings so user can see the sessions instead of the session's index
		        	 */
		            case 0: return registrant.getChildName();
		            case 1: return registrant.getCarerName();
		            case 2: return registrant.getBirthDate();
		            case 3: return registrant.getAddress();
		            case 4: return registrant.getAllergyInfo();
		            case 5: return registrant.getPhoneNumber();
		            case 6:	switch(registrant.getSession()){
		            		case 0: return sessionStrings[0];
		            		case 1: return sessionStrings[1];
		            		case 2: return sessionStrings[2];
		            		case 3: return sessionStrings[3];
		            		case 4: return sessionStrings[4];
		            		case 5: return sessionStrings[5];
		            		
		            		}
		            default: return null;
		        }
		    }
		     
		    @Override
		    public void setValueAt(Object value, int row, int column)
		    {
		        Registrant registrant = getRegistrant(row);
		     
		        switch (column)
		        {
		        	//all the columns except for the session data are editable
		            case 0: registrant.setChildName((String)value); break;
		            case 1: registrant.setCarerName((String)value); break;
		            case 2: registrant.setBirthDate((String)value); break;
		            case 3: registrant.setAddress((String)value); break;
		            case 4: registrant.setAllergyInfo((String)value); break;
		            case 5: registrant.setPhoneNumber((String)value); break;
		        }
		        fireTableCellUpdated(row, column);
		        //serialize the changed registrants arraylist back to file
		        serializeRegistrants();		        
		    }
		     
		    public Registrant getRegistrant(int row)
		    {
		        return registrants.get( row );
		    }

		 }

		 updateInfo.setLayout(new BorderLayout());
		 JTable table = new JTable(new RegistrantTableModel());
		 //table.setPreferredScrollableViewportSize(table.getPreferredSize());
		 JScrollPane scrollPane = new JScrollPane(table);
		 //set the row height of the table
		 table.setRowHeight(25);
		 JLabel guide = new JLabel("WARNING: All the columns except for \"Required session\" are editable, records will be changed immediately after editing.");
		 updateInfo.add(guide, BorderLayout.PAGE_START);
		 guide.setForeground(Color.red);
		 //add the JTable(scrollPane) to the updateInfo JPanel
		 updateInfo.add( scrollPane, BorderLayout.CENTER);
		 updateInfo.setVisible(true);
	}
	
	//this jpanel allows user to register children
	private void buildRegisterPanel(){		
		
		deserializeRegistrants();
		//set the layout of register jPanel as GridBagLayout	
		register.setLayout(new GridBagLayout());
		//declare the messages that ask user to enter certain types of personal data
		JLabel cNameLabel = new JLabel("Child's name:");
		JLabel pNameLabel = new JLabel("Parent/carer's name:");
		JLabel addressLabel = new JLabel("Address:");
		JLabel dobLabel = new JLabel("Child's birthdate (DD/MM/YYYY):");
		JLabel allergyLabel = new JLabel("Allergy info:");
		JLabel numberLabel = new JLabel("Contact number:");
		JLabel sessionLabel = new JLabel("Require session:");
		
		//declare the textfields for user to enter data		
		JTextField cNameField = new JTextField(25);
		JTextField pNameField = new JTextField(25);
		JTextField addressField = new JTextField(30);
		JTextField dobField = new JTextField(8);
		JTextField allergyField = new JTextField(30);
		JTextField numberField = new JTextField(13);
		
		
		//create a drop down list that allows user to choose their desired sessions
		JComboBox sessionList = new JComboBox(sessionStrings);
		//create the button for storing all the data to the record	
		JButton regButton = new JButton("Register");
		
		//gridbaglayout is used in order to put all the elements in the right places
        GridBagConstraints gc = new GridBagConstraints();
        //increase the margin between components
        gc.insets = new Insets(5,5,5,5);       
        gc.anchor = GridBagConstraints.LINE_START;
        gc.gridx = 0;
        gc.gridy = 0;
        register.add(cNameLabel, gc);
        gc.gridx = 1;
        gc.gridy = 0;
        register.add(cNameField, gc);

        gc.anchor = GridBagConstraints.LINE_START;
        gc.gridx = 0;
        gc.gridy = 1;
        register.add(pNameLabel, gc);
        gc.gridx = 1;
        gc.gridy = 1;
        register.add(pNameField, gc);
               
        gc.gridx = 0;
        gc.gridy = 2;
        register.add(dobLabel, gc);
        gc.gridx = 1;
        gc.gridy = 2;
        register.add(dobField, gc);
       
        gc.gridx = 0;
        gc.gridy = 3;
        register.add(addressLabel, gc);
        gc.gridx = 1;
        gc.gridy = 3;
        register.add(addressField, gc);

        gc.gridx = 0;
        gc.gridy = 4;
        register.add(allergyLabel, gc);
        gc.gridx = 1;
        gc.gridy = 4;
        register.add(allergyField, gc);
        
        gc.gridx = 0;
        gc.gridy = 5;
        register.add(numberLabel, gc);
        gc.gridx = 1;
        gc.gridy = 5;
        register.add(numberField, gc);
        
        gc.gridx = 0;
        gc.gridy = 6;
        register.add(sessionLabel, gc);
        gc.gridx = 1;
        gc.gridy = 6;
        register.add(sessionList, gc);
        
        gc.gridx = 1;
        gc.gridy = 7;
        register.add(regButton, gc);
        
        //create a action listener for the register button
        regButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		//count the amount of children that are registered in the selected age category
        		for(int i=0; i<registrants.size();i++){
        			if(registrants.get(i).getSession() == sessionList.getSelectedIndex()){
        				children++;
        			}
        		}
        		//registration is only allowed if there are less than 15 children in the selected category
        		if(children < 15){
	        		//add a new registrant object that stores the personal details that user put in the JTextFields
	        		registrants.add(new Registrant(cNameField.getText(), pNameField.getText(), dobField.getText(), addressField.getText(), allergyField.getText(), 
	        				numberField.getText(), sessionList.getSelectedIndex(), calculateAgeGroup(dobField.getText()), today, today, today.plusMonths(1), today.plusDays(7), false));
	        		serializeRegistrants();
	        		
	        		//create a formatter to format the decimals properly
	        		NumberFormat formatter = new DecimalFormat("#0.00");         		
	        		//write the invoice to a txt file named as the child's name
	        		PrintWriter invoice;
					try {
						invoice = new PrintWriter("Registration Invoices/" + cNameField.getText() + ".txt");
						invoice.println("Child's name: " + cNameField.getText());
						invoice.println("Carer's name: " + pNameField.getText());
						invoice.println("Child's date of birth: " + dobField.getText());
						invoice.println("Contact number: " + numberField.getText());
						invoice.println("Address: " + addressField.getText());
						invoice.println("Selected session: " + sessionList.getSelectedItem().toString());
						//get the registration fee (deposit and 25 pound fee)using the generateCharge method 
						//which returns the child's daily charge and times 7 which is the weekly fee and plus 25
						invoice.println("Fee (weekly fee deposit plus non-refundable £25): £" 
						+ formatter.format(generateCharge(sessionList.getSelectedIndex(), calculateAgeGroup(dobField.getText())) * 7 + 25));
						invoice.close();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
					//show message that says the child was successfully registered
					JOptionPane.showMessageDialog(null,
						    "Successfully registered this child.",
						    "Message",
						    JOptionPane.PLAIN_MESSAGE);
        		}else{
        			//show this message if the selected category is full
        			JOptionPane.showMessageDialog(null,
						    "This age category is full at the moment, the maximum intake of each category are 15 children.",
						    "Warning",
						    JOptionPane.PLAIN_MESSAGE);
        		}
        	}
        	
        });       
        
	}
	
	private int calculateAgeGroup(String dob){
		
		//childDob is the variable that stores the formatted child's birthday
		LocalDate childDob = null;
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try{
        	childDob = LocalDate.parse(dob, dateFormat);
        }catch (Exception e1){
        	System.out.println("User entered invalid date of birth for the child");
        }
        
        
        //ageSession can be integer 1-3 which represents the age group that the child is being put in depends on his/her age
        int ageSession = 0;
        
        if(childDob.isAfter(today.minusYears(2))){
        	ageSession = 1;
        }else if(childDob.isBefore(today.minusYears(2)) && childDob.isAfter(today.minusYears(3))){
        	ageSession = 2;
        }else if(childDob.isBefore(today.minusYears(3))){
        	ageSession = 3;
        }
        
        return ageSession;
	}
	
	
	//use this method to generate the charge, it takes the selected index of sessionList and the child's age group (refers to ageSession) as arguments
	private double generateCharge(int index, int ageSession){
		//call the method that deserialize data from file to the arraylist prices
		deserializePrices();
		deserializeRegistrants();
		double charge = 0;
		/*
		these three prices variables store the values of the first three objects of the prices arraylist,
		the values in each of the variable are the prices of each age group
		*/
		Prices age1 = prices.get(0);
		Prices age2 = prices.get(1);
		Prices age3 = prices.get(2);
		
		if(ageSession == 1){
			switch(index){
			//for child's age 0-2
			case 0: charge = age1.getAllDay(); break;
			case 1: charge = age1.getMorning(); break;
			case 2: charge = age1.getLunch(); break;
			case 3: charge = age1.getAfternoon(); break;
			case 4: charge = age1.getPreSchool(); break;
			}	
			
		}else if(ageSession == 2){
			switch(index){
			//for child's age 2-3
			case 0: charge = age2.getAllDay(); break;
			case 1: charge = age2.getMorning(); break;
			case 2: charge = age2.getLunch(); break;
			case 3: charge = age2.getAfternoon(); break;
			case 4: charge = age2.getPreSchool(); break;
			}	
		}else if(ageSession ==3){
			switch(index){
			//for child's age 3+
			case 0: charge = age3.getAllDay(); break;
			case 1: charge = age3.getMorning(); break;
			case 2: charge = age3.getLunch(); break;
			case 3: charge = age3.getAfternoon(); break;
			case 4: charge = age3.getPreSchool(); break;
			}	
		}
		return charge;
		
	}
	


}

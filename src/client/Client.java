package client;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class Client {

	private JFrame frame;	
	private JPanel connectionPanel;
	private JPanel shipSelectPanel;
	private JPanel gamePanel;
	
	
	private JFrame waitFrame;
	private JPanel waitPanel;

	
	
	private JButton continueButton;
	private JLabel descriptionLabel;

	private int[][] playerShips;
	private Player player;
//	private GameGrid yourGrid;
	private GameGrid enemyGrid;
//	private GameManager gameManager;
	
	private String host;
	private String port;
	private String name;
	private Socket socket;
	private ServerConnection serverCon;
		
	public Client() {
		
		// Connection panel
		connectionPanel = new JPanel();
        connectionPanel.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        
        JLabel connectLabel = new JLabel("Connect to server:");
        connectLabel.setFont(new Font("Verdana", Font.PLAIN, 24));
        connectLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel hostLabel = new JLabel("Host:");
        JLabel portLabel = new JLabel("Port:");
        JLabel nameLabel = new JLabel("Name:");
        
        JTextField  hostField = new JTextField();
        hostField.setPreferredSize(new Dimension(120, 30));
        JTextField  portField = new JTextField();
        portField.setPreferredSize(new Dimension(120, 30));
        JTextField  nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(120, 30));
        
        JButton connectButton = new JButton("Connect!");
		connectButton.addActionListener(e ->
		{
			String hostInput = hostField.getText();
			String portInput = portField.getText();
			String nameInput = nameField.getText();
			if(hostInput != null && !hostInput.isBlank() && portInput != null && !portInput.isBlank() && nameInput != null && !nameInput.isBlank())
			{
				host = hostInput;
				port = portInput;
				name = nameInput;
				System.out.println("Host: " + host);
				System.out.println("Port: " + port);
				System.out.println("Name: " + name);
				
				connectionPanel.setVisible(false);

				
				player = new Player(nameInput);
				
				// Sak savienojumu ar serveri
				try
				{
					socket = new Socket(host, Integer.parseInt(port));
					serverCon = new ServerConnection(socket, player, this);
					serverCon.start();
					
				}
				catch(IOException ex)
				{
					System.out.println("Error connection to server " + ex.getMessage());
					JOptionPane.showMessageDialog(frame, "Error connecting to server");
					frame.dispose();
					System.exit(1);
					
				}
				finally
				{
					frame.setTitle("Ship setup");
					frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
					frame.add(shipSelectPanel, BorderLayout.CENTER);
				}

				
			}
			else
			{
				JOptionPane.showMessageDialog(frame, "Host/port/name cannot be left blank.");
			}
		});
        
        g.insets = new Insets(5,5,5,5);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridwidth = 2;
        g.gridx = 0;
        g.gridy = 0;
        connectionPanel.add(connectLabel, g);
        g.gridwidth = 1;
        g.gridx = 0;
        g.gridy = 1;
        connectionPanel.add(hostLabel, g);
        g.gridx = 1;
        g.gridy = 1;
        connectionPanel.add(hostField, g);        
        g.gridx = 0;
        g.gridy = 2;
        connectionPanel.add(portLabel, g);
        g.gridx = 1;
        g.gridy = 2;
        connectionPanel.add(portField, g);
        g.gridx = 0;
        g.gridy = 3;
        connectionPanel.add(nameLabel, g);
        g.gridx = 1;
        g.gridy = 3;
        connectionPanel.add(nameField, g);
        g.gridx = 1;
        g.gridy = 4;
        connectionPanel.add(connectButton, g);
        
      
     
		waitFrame = new JFrame("Waiting for other player...");		
		waitPanel = new JPanel();
		


        JLabel waitLabel = new JLabel("Waiting for other player...");
        waitLabel.setFont(new Font("Verdana", Font.PLAIN, 24));
        waitLabel.setHorizontalAlignment(SwingConstants.CENTER);
        waitPanel.add(waitLabel);
        
		waitFrame.setLocationRelativeTo(null);
		waitFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		waitFrame.setLayout(new BorderLayout());
		waitFrame.setMinimumSize(new Dimension(500, 100));
		waitFrame.add(waitPanel, BorderLayout.CENTER);
		waitFrame.pack();
		
		
		
        // Setup Ship panel

	    
        // Initialize 2D array
        playerShips = new int[10][10]; // TODO: Fixed variables
        for (int i = 0; i < playerShips.length; i++) {
			for (int j = 0; j < playerShips[i].length; j++) {
				playerShips[i][j] = 0;
			}
		}
        
        

	    GameSetupGrid setupGrid = new GameSetupGrid(this);
	    
        shipSelectPanel = new JPanel();
        shipSelectPanel.setLayout(new GridBagLayout());
	    GridBagConstraints gbc2 = new GridBagConstraints();
        
        JLabel titleLabel1 = new JLabel("Setup your ships - press 'MOUSE2' to rotate.");
        titleLabel1.setFont(new Font("Verdana", Font.PLAIN, 24));
        titleLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        
        descriptionLabel = new JLabel("Place ship with size: 4");
        descriptionLabel.setFont(new Font("Verdana", Font.PLAIN, 18));
        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JButton continueButton = new JButton("Continue");
        this.continueButton = continueButton;
        continueButton.setVisible(false);
        continueButton.addActionListener(e ->
		{
			// Continue buttton pressed
			
			player.setPlayerField(setupGrid.returnField());
			player.setPlayerShips(setupGrid.getShips());
			
			
//			serverCon.sendPlayerName();
			serverCon.sendPlayerObject();
			serverCon.setReady(true);
//			serverCon.checkPlayersReady();
			
			
			
			
//			this.player = new Player(nameField.getText());
//			this.player.setPlayerField(setupGrid.returnField());
//			this.player.printPlayerField();
//			this.playerShips = setupGrid.returnField();
			
			
//	        this.yourGrid = new GameGrid(playerShips);
			PlayerGrid playerGrid = new PlayerGrid(player.getPlayerField());
	        this.enemyGrid = new GameGrid(playerShips);
	        this.player.setPlayerShips(setupGrid.getShips());
//			this.gameManager = new GameManager(this, this.yourGrid, this.enemyGrid, player, new Player("Enemy"));

			
	        
	        
	        
		    GridBagConstraints gbc3 = new GridBagConstraints();
		    
	        JLabel yourFieldLabel = new JLabel("Your field");
	        yourFieldLabel.setFont(new Font("Verdana", Font.PLAIN, 24));
	        yourFieldLabel.setHorizontalAlignment(SwingConstants.CENTER);
		    
	        JLabel enemyFieldLabel = new JLabel("Enemy field");
	        enemyFieldLabel.setFont(new Font("Verdana", Font.PLAIN, 24));
	        enemyFieldLabel.setHorizontalAlignment(SwingConstants.CENTER);
	        
	        gbc3.insets = new Insets(5,50,5,50);
	        gbc3.gridx = 0;
	        gbc3.gridy = 0;
	        gamePanel.add(yourFieldLabel, gbc3);
	        gbc3.gridx = 1;
	        gbc3.gridy = 0;
	        gamePanel.add(enemyFieldLabel, gbc3);	        
	        gbc3.gridx = 0;
	        gbc3.gridy = 1;
	        gamePanel.add(playerGrid, gbc3);
	        gbc3.gridx = 1;
	        gbc3.gridy = 1;
	        gamePanel.add(enemyGrid, gbc3);
	        
			shipSelectPanel.setVisible(false);
			frame.setTitle("Battleships - Playing on: " + host);
			frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
			frame.add(gamePanel, BorderLayout.CENTER);
		});
        
        gbc2.insets = new Insets(5,50,5,50);
        gbc2.gridx = 0;
        gbc2.gridy = 0;
        shipSelectPanel.add(titleLabel1, gbc2);
        gbc2.gridx = 0;
        gbc2.gridy = 1;
        shipSelectPanel.add(descriptionLabel, gbc2);
        gbc2.gridx = 0;
        gbc2.gridy = 2;
	    shipSelectPanel.add(setupGrid, gbc2);
        gbc2.gridx = 0;
        gbc2.gridy = 3;
        shipSelectPanel.add(continueButton, gbc2);
        
        
        // Setup Game panel
        gamePanel = new JPanel();
        gamePanel.setLayout(new GridBagLayout());

        
        
        frame = new JFrame("Connect to server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setMinimumSize(new Dimension(300, 200));
        frame.add(connectionPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
	

	}
	
	public int[][] getPlayerShips()
	{
		return playerShips;
	}
	
	public void editDescription(String newLabel)
	{
		descriptionLabel.setText(newLabel);
	}
	
	public void enableContinueButton()
	{
		continueButton.setVisible(true);
	}

	public static void main(String[] args) {
		new Client();

	}
	
	public void openWaitingScreen()
	{
		System.out.println("Other player not connected: opening waiting screen.");
		frame.setVisible(false);
		waitFrame.setVisible(true);
	}
	public void closeWaitingScreen()
	{
		frame.setVisible(true);
		if(waitFrame.equals(null))
			return;
		waitFrame.setVisible(false);

	}
	public void declareWinner(Player winner)
	{
		try
		{
			socket.close();
		}
		catch(IOException e)
		{
			System.out.println("Error closing client socket " + e.getMessage());
		}
		finally
		{
			JOptionPane.showMessageDialog(frame, winner.getPlayerName() + " has won.");
			frame.dispose();
		}
		
	}
	


}
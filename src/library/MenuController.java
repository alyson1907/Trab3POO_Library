package library;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MenuController implements Initializable
{
	//Stage primaryStage = (Stage) ((Button)event.getSource()).getScene().getWindow();
	static Library lb = new Library();

    @FXML
    private Text LblDate;
	
    @FXML
    private Button BtPrintBooks;

    @FXML
    private TextField TextNameUser;

    @FXML
    private TextField TextAuthorBook;

    @FXML
    private TextField TextTitleLoan;

    @FXML
    private Button BtRegisterUser;

    @FXML
    private Button BtRegisterLoan;

    @FXML
    private Button BtPrintLoans;

    @FXML
    private Button BtPrintUsers;

    @FXML
    private TextField TextRgUser;

    @FXML
    private TextField TextNameLoan;

    @FXML
    private TextField TextTitleBook;

    @FXML
    private Button BtRegisterBook;
    
    @FXML
    private TextField TextTypeUser;
    
    @FXML
    private TextField TextTypeBook;
    
    @FXML
    private TextField TextTypeLoan;
    
    @FXML
    private Label ErrorUser;
    
    @FXML
    private Label ErrorBooks;
    
    @FXML
    private Label ErrorLoan;
    
    @Override    
    public void initialize(URL url, ResourceBundle bundle) 
    {            
    	LblDate.setText(lb.formatter.format(lb.date));
    	
    } 
    
    @FXML
    void onClickRegisterUser(ActionEvent event) throws Exception
    {
    	if(TextTypeUser.getText().equals("student") || TextTypeUser.getText().equals("teacher") || TextTypeUser.getText().equals("comunity"))
		{
    		lb.registerUser("Users.csv", TextNameUser.getText(), TextRgUser.getText(), TextTypeUser.getText());
    		ErrorUser.setText("Registro inserido com sucesso!");
    		ErrorUser.setVisible(true);
		}
    	else
    	{	
    		ErrorUser.setText("Tipo inv�lido!");
    		ErrorUser.setVisible(true);
    	}
	}
    
    @FXML
    void onClickRegisterBook(ActionEvent event) throws Exception
    {
    	lb.registerBook("Books.csv", TextTitleBook.getText(), TextAuthorBook.getText(), TextTypeBook.getText());
    }

    @SuppressWarnings("deprecation")
	@FXML
    void onClickRegisterLoan(ActionEvent event) throws Exception
    {
    	if(TextTypeLoan.getText().equals("Loan") || TextTypeLoan.getText().equals("loan")) //Se o tipo for emprestimo
    	{
    		//Ao abrir MenuController, remover de ban.csv todos os usuarios que nao estao mais banidos (pela data atual)
    		//Falta verificar se o usuario esta banido
    		//Falta verificar se o livro existe na biblioteca
    		//Falta tratar quando digitar coisas invalidas nos campos
    		//Tratar um emprestimo por pessoa para cada titulo de livro
	    	ArrayList<User> usersList = lb.readUsers("Users.csv");
	    	ArrayList<Loan> loansList = lb.readLoans("Loans.csv");
	    	
	    	Date aux = new Date(lb.date.getYear(), lb.date.getMonth(), lb.date.getDate());
	    	
	    	String rg = TextNameLoan.getText();
	    	List<User> filteredList = usersList.stream().filter(u->u.getRg().equals(rg))
	    			.collect(Collectors.toList());
	    	User filteredUser = filteredList.get(0);
	    	
	    	long numberLoans = loansList.stream().filter(l->l.getRg().equals(rg))
	    			.filter(l->l.getOk().equals("false")).collect(Collectors.counting());
	    	
	    	if(filteredUser.getBanned().equals("false"))
	    	{
	    		if(filteredUser.getType().equals("teacher"))
	    		{
	    			if(numberLoans < 6)
	    			{
	    				aux.setDate(lb.date.getDate() + 60);
	    				lb.registerLoan("Loans.csv", TextNameLoan.getText(), TextTitleLoan.getText(), lb.formatter.format(aux));
	    			}
	    		}
	    		else if(filteredUser.getType().equals("student"))
	    		{
	    			if(numberLoans < 4)
	    			{
	    				aux.setDate(lb.date.getDate() + 15);
	    				lb.registerLoan("Loans.csv", TextNameLoan.getText(), TextTitleLoan.getText(), lb.formatter.format(aux));
	    			}
	    		}
	    		else if(filteredUser.getType().equals("comunity"))
	    		{
	    			if(numberLoans < 2)
	    			{
	    				aux.setDate(lb.date.getDate() + 15);
	    				lb.registerLoan("Loans.csv", TextNameLoan.getText(), TextTitleLoan.getText(), lb.formatter.format(aux));
	    			}
	    		}
	    	}
    	}
    	else if(TextTypeLoan.getText().equals("Devolution") || TextTypeLoan.getText().equals("devolution"))
    	{
    		ArrayList<Loan> loansList = lb.readLoans("Loans.csv"); //Lista de todos os emprestimos feitos
    		
    		List<Loan> filteredLoansList = loansList.stream().filter(l->l.getRg().equals(TextNameLoan.getText())).filter(l->l.getTitle()
    				.equals(TextTitleLoan.getText())).collect(Collectors.toList()); //Filtra todos os emprestimos ainda nao devolvidos feitos pelo RG passado,
        																				//com o livro de titulo passado
    		if(filteredLoansList.isEmpty())
    		{
    			//Tratar
    		}
    		else 
    		{
    			final Date today = new Date(lb.date.getYear(), lb.date.getMonth(), lb.date.getDate()); //Data atual
    			Date returnDate = new Date();
    			Loan loan = filteredLoansList.get(0);
    			returnDate = lb.formatter.parse(loan.getReturnDate()); //Data maxima de devolucao
    			
    			if(returnDate.equals(today) || returnDate.after(today)) //Se a entrega nao tiver atrasada
    			{
    				lb.removeLoan(TextNameLoan.getText(), TextTitleLoan.getText());
    			}
    			else
    			{
    				//calculando quantos dias de ban em counter
    				int counter = 0;
    				int day = today.getDate();
    				
    				while(!today.equals(returnDate))
    				{
    					day--;
    					today.setDate(day);
    					counter++;
    				}
    				//Calculando at� que dia est� banido
    				Date aux = new Date(lb.date.getYear(), lb.date.getMonth(), lb.date.getDate());
    				aux.setDate(aux.getDate() + counter);
    				//Escrevendo ban em Ban.csv
    				BufferedWriter bw = new BufferedWriter(new FileWriter("Bans.csv"));
    				bw.append(TextNameLoan.getText() + "," + lb.formatter.format(aux) + "\n"); //Escreve o RG e a data at� quando esta banido em Ban.csv
    				bw.close();
    				lb.removeLoan(TextNameLoan.getText(), TextTitleLoan.getText());
    			}
    		}
    	}
    }
    
    @FXML
    void onClickShowUsers(ActionEvent event) throws Exception
    {
    	Stage primaryStage = new Stage();
    	Parent root = FXMLLoader.load(getClass().getResource("UsersList.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
    }
    
    @FXML
    void onClickShowBooks(ActionEvent event) throws Exception
    {
    	Stage primaryStage = new Stage();
    	Parent root = FXMLLoader.load(getClass().getResource("BooksList.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
    }    
}
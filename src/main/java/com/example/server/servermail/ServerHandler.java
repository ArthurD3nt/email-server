package com.example.server.servermail;

import com.example.server.model.LogModel;
import com.example.server.model.UserService;
import com.example.transmission.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.stream.Collectors;


public class ServerHandler implements Runnable {

	private Socket socket;
	ObjectInputStream in;
	ObjectOutputStream out;
	private int counter;
	private LogModel logModel;
	private UserService userService;
	ArrayList<String> checkExist;
	User userReceiver;

	public ServerHandler(Socket socket, int counter, LogModel logModel) {
		this.logModel = logModel;
		this.socket = socket;
		this.counter = counter;
		this.userReceiver = null;
		this.checkExist = new ArrayList<>();

		try {
			InputStream inStream = socket.getInputStream();
			in = new ObjectInputStream(inStream);
			OutputStream outStream = socket.getOutputStream();
			out = new ObjectOutputStream(outStream);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		this.userService = UserService.getInstance(logModel, out);
	}

	private void connection(String email) throws IOException {
		logModel.setLog("Client " + email + " connected");
		User user = userService.readUserFromFile(email);

		if (user == null) {
			logModel.setLog("Client " + email + " :null, created!");
			user = userService.createUser(email);
			//out.writeObject(new Communication("user_created", new ErrorBody(email, "user not found")));
		}

		ArrayList<ArrayList<EmailBody>> arrayLists = new ArrayList<>();
		ArrayList<EmailBody> emailReceived = new ArrayList<>();
		ArrayList<EmailBody> emailSent = new ArrayList<>();
		ArrayList<EmailBody> emailBin = new ArrayList<>();

		user.getEmails().forEach(emailBody -> {
			if (emailBody.getReceivers().contains(email) && emailBody.getSender().equals(email) && !emailBody.getBin()) {
				emailReceived.add(emailBody);
				emailSent.add(emailBody);
			} else if (emailBody.getReceivers().contains(email) && !emailBody.getBin()) {
				emailReceived.add(emailBody);
			} else if (emailBody.getSender().equals(email) && !emailBody.getBin()) {
				emailSent.add(emailBody);
			} else if (emailBody.getBin()) {
				emailBin.add(emailBody);
			}
		});

		arrayLists.add(emailReceived);
		arrayLists.add(emailSent);
		arrayLists.add(emailBin);

		Communication response = new Communication("connection_ok", new ConnectionBody(user.getUser(), arrayLists));
		out.writeObject(response);
	}

	// private void sendEmail(EmailBody email) throws IOException {
	// 	this.checkExist.clear();
	// 	logModel.setLog("User " + email.getSender()
	// 			+ " sending email: " + email.getId()
	// 			+ " to: " + email.getReceivers().stream().collect(Collectors.joining(", ")));

	// 	// blocco il file del sender
	// 	User sender = userService.readUserFromFileBlocking(email.getSender());

	// 	if (sender == null) {
	// 		userService.unlock(email.getSender());
	// 		logModel.setLog("ERRORE: sender non trovato");
	// 		out.writeObject(new Communication("emails_not_saved", new BooleanBody(sender.getUser(), false)));
	// 		return;
	// 	}

	// 	for (String receiver : email.getReceivers()) {
	// 		try {
	// 			this.userReceiver = userService.readUserFromFileBlocking(receiver);

	// 			/* Gestione sender uguale a receiver: altrimenti scrive due volte sullo stesso file */
	// 			if (receiver.equals(email.getSender())) {
	// 				userService.unlock(receiver);
	// 				continue;
	// 			}

	// 			/* Se il receiver non è null allora scrivo sul file e poi fa l'unlock da solo*/
	// 			if (userReceiver != null) {
	// 				userReceiver.getEmails().add(email);
	// 				userService.writeUserToFile(userReceiver);
	// 			}

	// 		} catch (Exception e) {
	// 			userService.unlock(receiver);
	// 			logModel.setLog("ERRORE: " + receiver + " non trovato");
	// 			checkExist.add(receiver);
	// 		}

	// 	}

	// 	/* Se invia l'email almeno ad un receiver me la salvo tra le inviate*/
	// 	if (this.checkExist.size() < email.getReceivers().size() && this.checkExist.size() > 0) {
	// 		sender.getEmails().add(email);
	// 		userService.writeUserToFile(sender);
	// 		out.writeObject(new Communication("emails_saved_with_error", new EmailBody(null, checkExist, null, null)));
	// 	}
	// 	else if (this.checkExist.size() == email.getReceivers().size()) {
	// 		out.writeObject(new Communication("emails_not_saved", new EmailBody(null, checkExist, null, null)));
	// 	} else {
	// 		out.writeObject(new Communication("emails_saved", new BooleanBody(sender.getUser(), true)));
	// 	}
	// }

	private void sendEmail(EmailBody email) throws IOException {

		ArrayList<String> receiversNotExists = new ArrayList<>();

		logModel.setLog("User " + email.getSender()
				+ " sending email: " + email.getId()
				+ " to: " + email.getReceivers().stream().collect(Collectors.joining(", ")));

		// blocco il file del sender
		User sender = userService.readUserFromFileBlocking(email.getSender());

		if (sender == null) {
			userService.unlock(email.getSender());
			logModel.setLog("ERRORE: utente non trovato");
			out.writeObject(new Communication("emails_not_saved", new BooleanBody(sender.getUser(), false)));
			return;
		}

		for (String receiver : email.getReceivers()) {

			User userReceiver = userService.readUserFromFileBlocking(receiver);

			if (receiver.equals(email.getSender())) {
				userService.unlock(receiver);
				logModel.setLog("RECEIVER: " + receiver + " uguale a sender: " + email.getSender());
				continue;
			}
			else if (userReceiver == null) {
				userService.unlock(receiver);
				logModel.setLog("ERRORE: " + receiver + " non trovato");
				receiversNotExists.add(receiver);
				continue;
			}

			userReceiver.getEmails().add(email);
			userService.writeUserToFile(userReceiver);
		}

		if(receiversNotExists.size() > 0 && receiversNotExists.size() == email.getReceivers().size()){
			out.writeObject(new Communication("emails_not_saved", new EmailBody(email.getEmail(), receiversNotExists, null, null)));
		}
		else if(receiversNotExists.size() > 0 && receiversNotExists.size() < email.getReceivers().size()){
			/* Salva l'email solo se trova almeno un receiver*/
			sender.getEmails().add(email);
			userService.writeUserToFile(sender);
			out.writeObject(new Communication("emails_saved_with_error", new EmailBody(email.getEmail(), receiversNotExists, null, null)));
		}
		else {
			/* Salva l'email perché ha trovato tutti i receiver */
			sender.getEmails().add(email);
			userService.writeUserToFile(sender);
			out.writeObject(new Communication("emails_saved", new BooleanBody(sender.getUser(), true)));
		}
	}

	private void getNewEmails(GetEmailsBody getEmailsBody) throws IOException {
		User user = userService.readUserFromFile(getEmailsBody.getEmail());
		logModel.setLog("Get email by: " + getEmailsBody.getEmail());
		ArrayList<EmailBody> emailBodies = new ArrayList<>();

		if (user.getEmails().size() == 0) {
			out.writeObject(new Communication("no_emails", new EmailListBody(getEmailsBody.getEmail(), emailBodies)));
			return;
		}

		for (EmailBody e : user.getEmails()) {
			if (user.getEmails().isEmpty()) {
				out.writeObject(new Communication("no_emails", new EmailListBody(getEmailsBody.getEmail(), emailBodies)));
				return;
			}

			/* if:
			 * 1. controllo se il timestamp dell'email inviata è null
			 * 	 oppure il timestamp è dell'email che leggo è minore del timestamp dell'email inviata
			 *
			 * 2. se nell'email inviata non sono io il sender oppure sono tra i receiver
			 *
			 * aggiungo l'email
			 * */
			if ((getEmailsBody.getTimestamp() == null || e.getTimestamp().after(getEmailsBody.getTimestamp())) 
			/*&& 
			(!getEmailsBody.getEmail().equals(e.getSender()) || e.getReceivers().contains(getEmailsBody.getEmail()))*/) {
				emailBodies.add(e);
			}
		}

		out.writeObject(new Communication("get_emails_ok", new EmailListBody(getEmailsBody.getEmail(), emailBodies)));
	}

	private void moveToBin(String id, String email) throws IOException {
		User user = userService.readUserFromFileBlocking(email);
		for (EmailBody e : user.getEmails()) {
			if (e.getId().equals(id)) {
				e.setBin(true);
				logModel.setLog("Client: " + email + " moved to bin email with id " + id);
			}
		}
		userService.writeUserToFile(user);
		out.writeObject(new Communication("bin_ok", new BooleanBody(email, true)));

	}

	private void deletePermanently(String email) throws IOException {
		User user = userService.readUserFromFileBlocking(email);
		int i = 0;
		;

		if (user.getEmails().size() == 0) {
			out.writeObject(new Communication("delete_permanently_not_ok", new BooleanBody(email, false)));
		}

		while (i < user.getEmails().size()) {
			if (user.getEmails().get(i).getBin()) {
				logModel.setLog("Client: " + email + " remove email with id " + user.getEmails().get(i).getId());
				user.getEmails().remove(i);
			} else {
				i++;
			}
		}
		userService.writeUserToFile(user);

		out.writeObject(new Communication("delete_permanently_ok", new BooleanBody(email, true)));
	}

	@Override
	public void run() {

		try {
			try {
				/* Stream sono un flusso di dati
				 * InputStream: sono i byte che ricevo in input
				 * OutputStream: sono i byte che invio in input
				 * */

				try {
					if (in == null || out == null) return;

					Communication communication = (Communication) in.readObject();

					switch (communication.getAction()) {
						case "connection":
							connection(communication.getBody().getEmail());
							break;
						case "get_new_emails":
							getNewEmails((GetEmailsBody) communication.getBody());
							break;
						case "send_email":
							sendEmail((EmailBody) communication.getBody());
							break;
						case "bin":
							BinBody b = (BinBody) communication.getBody();
							moveToBin(b.getId(), b.getEmail());
							break;
						case "delete":
							deletePermanently(communication.getBody().getEmail());
							break;
					}
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}

			} finally {
				System.out.println("FINITO");
				logModel.setLog("Client disconnected");
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

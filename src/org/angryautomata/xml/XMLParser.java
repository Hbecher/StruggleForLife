package org.angryautomata.xml;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.angryautomata.game.Automaton;
import org.angryautomata.game.Player;
import org.angryautomata.game.Position;
import org.angryautomata.game.scenery.Scenery;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLParser
{
	private final List<File> xmlFiles;
	private List<Player> players = null;

	public XMLParser(List<File> xmlFiles)
	{
		this.xmlFiles = xmlFiles;
	}

	public void parse()
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;

		try
		{
			parser = factory.newSAXParser();
		}
		catch(SAXException | ParserConfigurationException e)
		{
			e.printStackTrace();

			return;
		}

		players = new ArrayList<>();
		int playerNumber = 0;

		for(File xmlFile : xmlFiles)
		{
			Handler handler = new Handler(xmlFile.getName(), playerNumber);

			try
			{
				parser.parse(xmlFile, handler);
			}
			catch(SAXException | IOException e)
			{
				e.printStackTrace();

				continue;
			}

			Player player = handler.getPlayer();
			String playerName = player.getName();
			boolean exists = false;

			for(Player p : players)
			{
				if(p.getName().equals(playerName))
				{
					exists = true;

					break;
				}
			}

			if(exists)
			{
				System.out.println("Le joueur '" + playerName + "' existe déjà, ignoré");
			}
			else
			{
				players.add(handler.getPlayer());

				playerNumber++;
			}
		}
	}

	public List<Player> getPlayers()
	{
		return players;
	}

	private static class Handler extends DefaultHandler
	{
		private final String fileName;
		private final int playerNumber;
		private Player player = null;
		private int[][] transitions = null, actions = null;
		private String currentTag = null;
		private String name = null;
		private int states = 0;

		Handler(String fileName, int playerNumber)
		{
			this.fileName = fileName;
			this.playerNumber = playerNumber;
		}

		@Override
		public void startDocument() throws SAXException
		{
			System.out.println("Lecture de '" + fileName + "'");
		}

		@Override
		public void endDocument() throws SAXException
		{
			if(transitions == null || actions == null || name == null || states <= 0)
			{
				throw new SAXException("Le fichier '" + fileName + "' est invalide");
			}

			player = new Player(new Automaton(transitions, actions, Position.ORIGIN), name, playerNumber);
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
			currentTag = qName;
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException
		{
			currentTag = null;
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException
		{
			if(currentTag == null || currentTag.isEmpty())
			{
				return;
			}

			String value = String.valueOf(ch, start, length).trim();

			switch(currentTag)
			{
				case "nom":
				{
					name = value;

					break;
				}

				case "nb_symbole":
				{
					int k;

					try
					{
						k = Integer.parseInt(value);
					}
					catch(NumberFormatException e)
					{
						throw new SAXException("Erreur dans '" + fileName + "' : le nombre de symboles doit être un nombre");
					}

					if(k != Scenery.sceneries())
					{
						throw new SAXException("Erreur dans '" + fileName + "' : le nombre de symboles est différent de celui de l'application");
					}
				}

				case "nb_etat":
				{
					int k;

					try
					{
						k = Integer.parseInt(value);
					}
					catch(NumberFormatException e)
					{
						throw new SAXException("Erreur dans '" + fileName + "' : le nombre d'états doit être un nombre");
					}

					if(k <= 0)
					{
						throw new SAXException("Erreur dans '" + fileName + "' : le nombre d'états doit être strictement positif");
					}

					states = k;

					break;
				}

				case "transitions":
				{
					if(states <= 0)
					{
						throw new SAXException("Erreur dans '" + fileName + "' : mauvaise hiérarchie du XML");
					}

					transitions = new int[Scenery.sceneries()][states];
					actions = new int[Scenery.sceneries()][states];

					break;
				}

				case "transition":
				{
					String[] trans = value.split(",", 4);
					int state, symbol, action, next;

					try
					{
						state = Integer.parseInt(trans[0]);
						symbol = Integer.parseInt(trans[1]);
						action = Integer.parseInt(trans[2]);
						next = Integer.parseInt(trans[3]);
					}
					catch(NumberFormatException e)
					{
						throw new SAXException("Erreur dans '" + fileName + "' : mauvaise syntaxe de transition");
					}

					if(state < 0 || state >= states || symbol < 0 || symbol >= Scenery.sceneries())
					{
						throw new SAXException("Erreur dans '" + fileName + "' : transition invalide");
					}

					transitions[symbol][state] = next;
					actions[symbol][state] = action;

					break;
				}
			}
		}

		Player getPlayer()
		{
			return player;
		}
	}
}

package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {

	Graph<Fermata, DefaultEdge> grafo;

	Map<Fermata, Fermata> predecessori;

	public void creaGrafo() {
		this.grafo = new SimpleGraph<>(DefaultEdge.class);

		MetroDAO dao = new MetroDAO();
		List<Fermata> fermate = dao.getAllFermate();

		/*
		 * for (Fermata f : fermate) { this.grafo.addVertex(f); }
		 */

		Graphs.addAllVertices(this.grafo, fermate);

		// Aggiungi archi

		/*
		 * for (Fermata f1 : this.grafo.vertexSet()) { for (Fermata f2 :
		 * this.grafo.vertexSet()) { if (!f1.equals(grafo) && dao.fermateCollegate(f1,
		 * f2)) { this.grafo.addEdge(f1, f2); } } }
		 */

		List<Connessione> connessioni = dao.getAllConnessione(fermate);

		for (Connessione c : connessioni) {
			this.grafo.addEdge(c.getStazP(), c.getStazA());
		}

		System.out.println("Grafo creato con " + this.grafo.vertexSet().size() + " vertici e "
				+ this.grafo.edgeSet().size() + " archi");

		// Fermata f = null;

		/*
		 * Set<DefaultEdge> archi = this.grafo.edgesOf(f); for(DefaultEdge e : archi) {
		 * /*Fermata f1 = grafo.getEdgeSource(e); //oppure Fermata f2 =
		 * grafo.getEdgeTarget(e); Fermata f1 = Graphs.getOppositeVertex(grafo, e, f);
		 * 
		 * }
		 */

		// List<Fermata> fermateAdiacenti = Graphs.successorListOf(grafo, f);

	}

	public List<Fermata> fermateRaggiungibili(Fermata partenza) {

		BreadthFirstIterator<Fermata, DefaultEdge> bfv = new BreadthFirstIterator<>(grafo, partenza);

		DepthFirstIterator<Fermata, DefaultEdge> dfv = new DepthFirstIterator<>(grafo, partenza);
		List<Fermata> result = new ArrayList<>();

		this.predecessori = new HashMap<>();
		this.predecessori.put(partenza, null);

		bfv.addTraversalListener(new TraversalListener<Fermata, DefaultEdge>() {

			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
			}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
			}

			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> e) {
				DefaultEdge arco = e.getEdge();
				Fermata a = grafo.getEdgeSource(arco);
				Fermata b = grafo.getEdgeTarget(arco);
				if (predecessori.containsKey(b) && !predecessori.containsKey(a)) {
					predecessori.put(a, b);
					//System.out.println(a + " scoperto da "+ b);
				}else if (predecessori.containsKey(a) && !predecessori.containsKey(b)){
					predecessori.put(b, a);
					//System.out.println(b + " scoperto da "+ a);
				}

			}

			@Override
			public void vertexTraversed(VertexTraversalEvent<Fermata> e) {
				/*
				 * System.out.println(e.getVertex()); Fermata nuova = e.getVertex(); Fermata
				 * precendente = (Fermata) e.getSource(); predecessori.put(nuova, precendente);
				 */
			}

			@Override
			public void vertexFinished(VertexTraversalEvent<Fermata> e) {
			}

		});

		while (bfv.hasNext()) {
			Fermata f = bfv.next();
			result.add(f);
		}

		return result;
	}

	public Fermata trovaFermata(String nome) {
		for (Fermata f : this.grafo.vertexSet()) {
			if (f.getNome().equals(nome)) {
				return f;
			}
		}
		return null;
	}

	public List<Fermata> trovaCammino(Fermata partenza, Fermata arrivo) {

		fermateRaggiungibili(partenza);

		List<Fermata> cammino = new LinkedList<>();

		cammino.add(arrivo);
		Fermata f = arrivo;
		while (predecessori.get(f) != null) {
			f = predecessori.get(f);
			cammino.add(0, f);
		}
		
		
		return cammino;
	}

}







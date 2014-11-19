package factory.station;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.wrapper.ControllerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import factory.order.Order;
import factory.visualization.VisualizationAdapter;

abstract class AbstractAssemblyStation extends AbstractStation {
	
	private static final long serialVersionUID = -2934606720355101360L;
	private static final ThreadedBehaviourFactory THREADED_BEHAVIOUR_FACTORY = new ThreadedBehaviourFactory();
	private static final Logger LOG = LoggerFactory.getLogger(AbstractAssemblyStation.class);
	
	@Override
	protected void setup() {
		super.setup();
		
		// add behaviours
		this.addBehaviour(THREADED_BEHAVIOUR_FACTORY.wrap(new AssembleBehaviour(this)));
	}
	
	protected void assemble(Order order) throws InterruptedException {
		Thread.sleep(500);
		order.assemble(getServiceType());
	}
	
	/**
	 * Assembles the next order in queue.
	 */
	private class AssembleBehaviour extends CyclicBehaviour {
		
		private static final long serialVersionUID = 4362396144651504823L;
		
		public AssembleBehaviour(Agent agent) {
			super(agent);
		}

		@Override
		public void action() {
			try {
				final AID orderAid = inQueue.take();
				VisualizationAdapter.visualizeStationQueueChange(AbstractAssemblyStation.this.getLocalName(), inQueue.size(), outQueue.size());
				
				final Order order = getContainerController().getAgent(orderAid.getName(), true).getO2AInterface(Order.class);
				assemble(order);

				outQueue.put(orderAid);
				VisualizationAdapter.visualizeStationQueueChange(AbstractAssemblyStation.this.getLocalName(), inQueue.size(), outQueue.size());
			} catch (InterruptedException | ControllerException e) {
				LOG.error("Assembly failed.", e);
			}
		}
		
	}

}

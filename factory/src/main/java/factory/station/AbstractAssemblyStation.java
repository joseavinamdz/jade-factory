package factory.station;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jade.core.behaviours.CyclicBehaviour;
import factory.order.Order;

abstract class AbstractAssemblyStation extends AbstractStation {
	
	private static final long serialVersionUID = -2934606720355101360L;
	private static final Logger LOG = LoggerFactory.getLogger(AbstractAssemblyStation.class);
	
	@Override
	protected void setup() {
		super.setup();
		
		// add behaviours
		this.addBehaviour(new AssemblyBehaviour());
	}
	
	protected abstract void assemble(Order order) throws InterruptedException;
	
	/**
	 * Assembles the next order in queue.
	 */
	private class AssemblyBehaviour extends CyclicBehaviour {
		
		private static final long serialVersionUID = 4362396144651504823L;

		@Override
		public void action() {
			try {
				final Order order = takeNextOrder();
				LOG.info("Started assembly of order " + order);
				assemble(order);
				putFinishedOrder(order);
				LOG.info("Finished assembly of order " + order);
			} catch (InterruptedException e) {
				LOG.error("Assembly failed.", e);
			}
		}
		
	}

}
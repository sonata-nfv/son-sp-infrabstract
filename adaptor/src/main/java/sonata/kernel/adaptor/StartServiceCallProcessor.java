package sonata.kernel.adaptor;

import sonata.kernel.adaptor.messaging.ServicePlatformMessage;

public class StartServiceCallProcessor extends AbstractCallProcessor {

	public StartServiceCallProcessor(ServicePlatformMessage message, String UUID, AdaptorMux mux) {
		super(message, UUID, mux);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean process(ServicePlatformMessage message) {
		
		//TODO implement wrapper selection based on request body
		
		//TODO parse the NSD/VNFD from the request body
		
		//TODO use wrapper interface to send the NSD/VNFD, along with meta-data to the wrapper, triggering the service instantiation.
		
		return false;
	}

}

// Written by William Cook, Ben Wiedermann, Ali Ibrahim
// The University of Texas at Austin, Department of Computer Science
// See LICENSE.txt for license information
package batch;

import batch.util.BatchFactory;
import batch.util.ForestWriter;

// TODO: this should be an interface!!!
public abstract class Fun<S, T> {
	public abstract T apply(S x);
	
	// TODO: this should be autogenerated~!!~!!!
	public <E> E apply$getRemote(ForestWriter in, BatchFactory<E> factory, E x) {
		throw new Error("Missing apply$getRemote");
	}
}

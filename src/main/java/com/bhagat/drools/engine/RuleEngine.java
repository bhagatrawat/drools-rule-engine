package com.bhagat.drools.engine;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;

import com.bhagat.drools.domain.CartItem;
import com.bhagat.drools.domain.Customer;
import com.bhagat.drools.domain.Product;


/**
 * The RuleEngine executes the rules.
 */
public class RuleEngine {

    public static final void main(String[] args) {
    	KieSession kSession = null;
    	try {
        	
            // Instantiate KIE Service and create a new rule session
    		KieServices ks = KieServices.Factory.get();
    		KieContainer kContainer  = ks.getKieClasspathContainer();
            kSession = kContainer.newKieSession("rules-session");

            /**
             * Register Events
             */
            kSession.addEventListener(new RuleRuntimeEventListener() {
				public void objectInserted(ObjectInsertedEvent event) {
					if(event.getObject() instanceof CartItem ){
						System.out.println(" ############ Rule is Inserted: "+ event.getObject());
					}
				}
				public void objectUpdated(ObjectUpdatedEvent event) {
					System.out.println("############  Rule is Updated: "+ event.getRule().getName());
				}
				public void objectDeleted(ObjectDeletedEvent event) {
					System.out.println("############  Rule is Deleted: "+ event.getRule().getName());				}
			});
    		
            
            /**
             * Create domain objects
             */
            Customer customer = Customer.newCustomer("RS");
    		Product p1 = new Product("Macbook Pro", 15000);
    		Product p2 = new Product("Samsung Glaxy S5", 5000);
    		p2.setRequiresRegistration(true);
    		Product p3 = new Product("Kindle", 2000);
    		
    		Product p4OutOfStock = new Product("Television", 2000);
    		p4OutOfStock.setAvailableQty(0);
    		
    		Product p5 = new Product("Electronic", 10000);
    		p5.setAvailableQty(2);
    		
    		customer.addItem(p1, 1);
    		customer.addItem(p2, 2);
    		customer.addItem(p3, 5);
    		customer.setCoupon("SUMMER15");
    		
    		//Insert domain objects to knowledge session
    		List<CartItem> cartItems = customer.getCart().getCartItems();
    		for (CartItem cartItem: cartItems) {
    			kSession.insert(cartItem);
    		}
    		System.out.println("#### Fire All Rules ####");
            kSession.fireAllRules(); 
            System.out.println("-------------------------------------");
            System.out.println("Customer cart\n" + customer);
            
            Customer newCustomer = Customer.newCustomer("Bhagat");
    		newCustomer.addItem(p1, 1);
    		newCustomer.addItem(p2, 2);
    		newCustomer.addItem(p4OutOfStock, 1);
    		newCustomer.addItem(p5, 10);    		
    		
    		cartItems = newCustomer.getCart().getCartItems();
    		for (CartItem cartItem: cartItems) {
    			kSession.insert(cartItem);
    		}
    		kSession.insert(newCustomer.getCart());
    		kSession.setGlobal("outOfStockProducts", new ArrayList<Product>());
    		
    		
    		System.out.println("#### Fire Single Rule ####");
            //kSession.fireAllRules();
    		kSession.fireAllRules(new AgendaFilter() {
							public boolean accept(Match match) {
					if("If new, 2% discount".equalsIgnoreCase(match.getRule().getName())){
						System.out.println("Firing Rule: "+ match.getRule().getName());	
						return true;
					}
					return false;
				}
			});
            System.out.println("---------------------------------------------");
            System.out.println("Customer cart\n" + customer);
                        
        } catch (Throwable t) {
            t.printStackTrace();
        }finally{
        	if(kSession != null) kSession.destroy();
        }
    }
}
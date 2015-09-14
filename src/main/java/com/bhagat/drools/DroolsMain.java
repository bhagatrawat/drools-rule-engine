package com.bhagat.drools;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.runtime.KieSession;

import com.bhagat.drools.domain.CartItem;
import com.bhagat.drools.domain.Customer;
import com.bhagat.drools.domain.Product;
import com.bhagat.drools.engine.RuleEngine;

public class DroolsMain {
	public static void main(String[] args) {
		KieSession kSession = null;
		try {

			kSession = RuleEngine.getKieSession();
			RuleEngine.registerKSessionEvents();

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

			// Insert domain objects to knowledge session
			List<CartItem> cartItems = customer.getCart().getCartItems();
			for (CartItem cartItem : cartItems) {
				RuleEngine.insert(cartItem);
			}
			System.out.println("#### Fire All Rules ####");
			RuleEngine.fireAllRules();
			System.out.println("-------------------------------------");
			System.out.println("Customer cart\n" + customer);

			Customer newCustomer = Customer.newCustomer("Bhagat");
			newCustomer.addItem(p1, 1);
			newCustomer.addItem(p2, 2);
			newCustomer.addItem(p4OutOfStock, 1);
			newCustomer.addItem(p5, 10);

			cartItems = newCustomer.getCart().getCartItems();
			for (CartItem cartItem : cartItems) {
				RuleEngine.insert(cartItem);
			}
			RuleEngine.insert(newCustomer.getCart());
			kSession.setGlobal("outOfStockProducts", new ArrayList<Product>());

			System.out.println("#### Fire Single Rule ####");
			RuleEngine.fireSingleRule("If new, 2% discount");
			System.out.println("---------------------------------------------");
			System.out.println("Customer cart\n" + customer);

		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			if (kSession != null)
				kSession.destroy();
		}
	}
}

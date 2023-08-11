import { Address } from "@stripe/stripe-js";
import { BillingAddress } from "../auth-service/user-details";
import { Item } from "src/app/shop/item.model";

export interface ReducedPaymentDetails{
    paymentReference: string;
    billingAddress: BillingAddress;
    address: Address;
    items: ItemQuantity[];
    paymentMethod: string;
    errorMessage: string;
    createdDate: Date;
    totalPrice: number;
    currency: string;

}

export interface ItemQuantity{
    item: Item;
    quantity: number;
}
export interface ChargeRequest{
    userId: number;
    itemQuantities: Map<number, number>;
    billingAddressId: number;
    shippingAddressId: number; 
}

export interface PaymentIntentResponse{
    client_secret: string;
    amount: number;
    currency: string;
    status: string;
    id: string;
}
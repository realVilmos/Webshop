export interface UserDetails{
    user_id: number;
    email: string;
    first_name: string;
    last_name: string;
    role: string;
    access_token: string;
    refresh_token: string;
}

export interface Address{
    id: number;
    county: String;
    postalCode: number;
    city: String;
    street: String;
    phoneNumber: String;
}

export interface BillingAddress{
    id: number;
    address: Address;
    companyName: String;
    taxNumber: String;
}
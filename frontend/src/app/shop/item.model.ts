export interface Item {
    id: number;
    name: string;
    description: string;
    category: string;
    imgUrl: string[];
    weight: number;
    dimensions: string;
    averageRating: number;
    reviewCount: number;
    manufacturer: string;
    price: ItemPrice;
    reviews: Review[];
    vendor: number;
  }
  
  export interface ItemPrice {
    originalPrice: number;
    salePrice: number;
    onSale: boolean;
    saleEndDate: Date;
  }
  
  export interface Review {
    id: number;
    rating: number;
    comment: string;
    reviewDate: Date;
    reviewerName: string;
  }
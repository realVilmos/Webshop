import { ItemPrice } from "./item.model";

export interface ReducedItemModel{
    imgUrl: string;
    name: string;
    itemPrice: ItemPrice;
    rating: number;
    id: number;
}
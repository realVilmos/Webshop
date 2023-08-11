export interface Category{
    id: number,
    name: String,
    partentId: number;
    subCategories: Category[];
}
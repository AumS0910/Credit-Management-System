interface MenuItem {
  id: string;
  name: string;
  description: string;
  imageUrl: string;
  price: number;
  rating?: number; // Add this line
  popularity?: string;
  category?: string;
  type?: string;
}
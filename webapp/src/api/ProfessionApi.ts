import apiClient from './ApiClient';
import type { ProfessionItem } from '@/models/ProfessionItem';
import type { ProfessionNameItem } from '@/models/ProfessionNameItem';

const API_URL = 'professions';

export const professionApi = {
  async getAll(): Promise<ProfessionItem[]> {
    const response = await apiClient.get<ProfessionItem[]>(API_URL);
    return response.data;
  },

  async getAllNames(): Promise<ProfessionNameItem[]> {
    const response = await apiClient.get<ProfessionNameItem[]>(`${API_URL}/all_names`);
    return response.data;
  },

  async add(item: Omit<ProfessionItem, 'id'>): Promise<ProfessionItem> {
    const response = await apiClient.post<ProfessionItem>(API_URL, item);
    return response.data;
  },

  async update(item: ProfessionItem): Promise<ProfessionItem> {
    const response = await apiClient.patch<ProfessionItem>(`${API_URL}/${item.id}`, item);
    return response.data;
  },

  async delete(id: number): Promise<void> {
    await apiClient.delete(`${API_URL}/${id}`);
  }
};

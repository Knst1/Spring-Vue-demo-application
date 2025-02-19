import apiClient from './ApiClient';
import type { DepartmentItem } from '@/models/DepartmentItem';
import type { DepartmentNameItem } from '@/models/DepartmentNameItem';

const API_URL = 'departments';

export const departmentApi = {
  async getAll(): Promise<DepartmentItem[]> {
    const response = await apiClient.get<DepartmentItem[]>(API_URL);
    return response.data;
  },

  async getAllowedParentList(id: number): Promise<DepartmentNameItem[]> {
    const response = await apiClient.get<DepartmentNameItem[]>(`${API_URL}/allowed_parents/${id}`);
    return response.data;
  },

  async getAllNames(): Promise<DepartmentNameItem[]> {
    const response = await apiClient.get<DepartmentNameItem[]>(`${API_URL}/all_names`);
    return response.data;
  },

  async add(item: Omit<DepartmentItem, 'id'>): Promise<DepartmentItem> {
    const response = await apiClient.post<DepartmentItem>(API_URL, item);
    return response.data;
  },

  async update(item: DepartmentItem): Promise<DepartmentItem> {
    const response = await apiClient.patch<DepartmentItem>(`${API_URL}/${item.id}`, item);
    return response.data;
  },

  async delete(id: number): Promise<void> {
    await apiClient.delete(`${API_URL}/${id}`);
  }
};

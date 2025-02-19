<template>
  <div class="employee-page">
    <LoadingSpinner v-if="loading" />

    <a-table v-else :columns="columns" :data-source="data" :pagination="false" bordered>
      <template #title>
        <div class="table-title">
          <span>Сотрудники</span>
          <a-button type="primary" @click="openAddModal">Добавить</a-button>
        </div>
      </template>

      <template #bodyCell="{ column, record }">
        <span v-if="column.dataIndex === 'profession'">
          {{ getProfessionName(record.profession) }}
        </span>
        <span v-else-if="column.dataIndex === 'department'">
          {{ getDepartmentName(record.department) }}
        </span>
        <span v-else-if="column.dataIndex !== 'actions'">
          {{ record[column.dataIndex] }}
        </span>
        <span v-else>
          <a-space>
            <a-button shape="round" @click="openEditModal(record)">
              <template #icon>
                <EditOutlined />
              </template>
            </a-button>
            <a-button danger shape="round" @click="openDeleteModal(record)">
              <template #icon>
                <DeleteOutlined />
              </template>
            </a-button>
          </a-space>
        </span>
      </template>
    </a-table>

    <EmployeeFormModal
      v-if="open"
      :open="open"
      :employee="selectedEmployee"
      :is-edit="isEditMode"
      @close="closeFormModal"
      @submit="handleFormSubmit"
    />

    <a-modal
      v-model:open="isDeleteModalVisible"
      title="Подтверждение удаления"
      @ok="confirmDelete"
      @cancel="closeDeleteModal"
      okText="Удалить"
      cancelText="Отмена"
    >
      <p>Вы уверены, что хотите удалить сотрудника "{{ selectedEmployee?.name }}"?</p>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
  import { ref, onMounted } from 'vue'
  import { employeeApi } from '@/api/EmployeeApi'
  import { professionApi } from '@/api/ProfessionApi'
  import { departmentApi } from '@/api/DepartmentApi'
  import LoadingSpinner from '@/components/LoadingSpinner.vue'
  import { message } from 'ant-design-vue'
  import { EditOutlined, DeleteOutlined } from '@ant-design/icons-vue'
  import EmployeeFormModal from '@/components/modals/EmployeeFormModal.vue'
  import type { EmployeeItem } from '@/models/EmployeeItem'
  import type { ProfessionNameItem } from '@/models/ProfessionNameItem'
  import type { DepartmentNameItem } from '@/models/DepartmentNameItem'

  const data = ref<EmployeeItem[]>([])
  const loading = ref(true)

  const professionsList = ref<ProfessionNameItem[]>([])
  const departmentsList = ref<DepartmentNameItem[]>([])

  const columns = [
    { title: 'ФИО', dataIndex: 'name' },
    { title: 'Профессия', dataIndex: 'profession' },
    { title: 'Отдел', dataIndex: 'department' },
    { title: 'Примечание', dataIndex: 'description' },
    { title: 'Действия', dataIndex: 'actions' },
  ]

  onMounted(async () => {
    try {
      professionsList.value = await professionApi.getAllNames()
    } catch (error) {
      console.error('Ошибка загрузки профессий:', error)
      message.error('Ошибка загрузки профессий')
    }
    try {
      departmentsList.value = await departmentApi.getAllNames()
    } catch (error) {
      console.error('Ошибка загрузки отделов:', error)
      message.error('Ошибка загрузки отделов')
    }
    try {
      data.value = await employeeApi.getAll()
    } catch (error) {
      console.error('Ошибка загрузки данных:', error)
    } finally {
      loading.value = false
    }
  })

  const open = ref(false)
  const isEditMode = ref(false)
  const selectedEmployee = ref<EmployeeItem | null>(null)

  const openAddModal = () => {
    isEditMode.value = false
    selectedEmployee.value = null
    open.value = true
  }

  const openEditModal = (record: EmployeeItem) => {
    isEditMode.value = true
    selectedEmployee.value = { ...record }
    open.value = true
  }

  const closeFormModal = () => {
    open.value = false
  }

  const handleFormSubmit = async (employee: EmployeeItem) => {
    try {
      if (isEditMode.value && selectedEmployee.value) {
        const updated = employee
        data.value = data.value.map((item) => (item.id === updated.id ? updated : item))
        message.success('Запись о сотруднике обновлена')
      } else {
        const added = employee
        data.value.push(added)
        message.success('Запись о сотруднике добавлена')
      }
    } catch (error) {
      console.error('Ошибка сохранения:', error)
    } finally {
      open.value = false
    }
  }

  const isDeleteModalVisible = ref(false)

  const openDeleteModal = (record: EmployeeItem) => {
    selectedEmployee.value = record
    isDeleteModalVisible.value = true
  }

  const closeDeleteModal = () => {
    isDeleteModalVisible.value = false
  }

  const confirmDelete = async () => {
    if (!selectedEmployee.value) return
    try {
      await employeeApi.delete(selectedEmployee.value.id)
      data.value = data.value.filter((item) => item.id !== selectedEmployee.value!.id)
      message.success('Сотрудник удалён')
    } catch (error) {
      console.error('Ошибка удаления:', error)
      message.error('Ошибка удаления')
    } finally {
      isDeleteModalVisible.value = false
    }
  }

  const getProfessionName = (id: number) => {
    const profession = professionsList.value.find((p) => p.id === id)
    return profession ? profession.name : 'Неизвестная профессия'
  }

  const getDepartmentName = (id: number) => {
    const department = departmentsList.value.find((d) => d.id === id)
    return department ? department.name : 'Неизвестный отдел'
  }
</script>

<style scoped>
  .employee-page {
    position: relative;
    padding: 16px;
  }

  .table-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
</style>

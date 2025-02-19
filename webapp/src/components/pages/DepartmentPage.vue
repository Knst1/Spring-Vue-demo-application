<template>
  <div class="department-page">
    <LoadingSpinner v-if="loading" />

    <a-table v-else :columns="columns" :data-source="flattenedData" :pagination="false" bordered>
      <template #title>
        <div class="table-title">
          <span>Отделы</span>
          <a-button type="primary" @click="openAddModal">Добавить</a-button>
        </div>
      </template>

      <template #bodyCell="{ column, record }">
        <span v-if="column.dataIndex !== 'name' && column.dataIndex !== 'actions'">
          {{ record[column.dataIndex] }}
        </span>
        <span v-if="column.dataIndex === 'name'">
          <span :style="{ marginLeft: record.indent * 20 + 'px' }">
            <template v-if="record.isParent">
              <span v-if="record.expanded" @click="toggleExpand(record)">
                <CaretDownOutlined />
              </span>
              <span v-else @click="toggleExpand(record)">
                <CaretRightOutlined />
              </span>
            </template>
            <template v-else>
              <span></span>
            </template>
            {{ record.name }}
          </span>
        </span>
        <span v-if="column.dataIndex === 'actions'">
          <a-space>
            <a-button shape="round" @click="openEditModal(record)" v-if="record.id !== undefined">
              <template #icon>
                <EditOutlined />
              </template>
            </a-button>
            <a-button
              v-if="!record.hasSubDepartments && record.id !== undefined"
              danger
              shape="round"
              @click="openDeleteModal(record)"
            >
              <template #icon>
                <DeleteOutlined />
              </template>
            </a-button>
          </a-space>
        </span>
      </template>
    </a-table>

    <DepartmentFormModal
      v-if="open"
      :open="open"
      :department="selectedDepartment"
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
      <p>Вы уверены, что хотите удалить отдел "{{ selectedDepartment?.name }}"?</p>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
  import { ref, onMounted, computed } from 'vue'
  import { departmentApi } from '@/api/DepartmentApi'
  import LoadingSpinner from '@/components/LoadingSpinner.vue'
  import { message } from 'ant-design-vue'
  import {
    EditOutlined,
    DeleteOutlined,
    CaretRightOutlined,
    CaretDownOutlined,
  } from '@ant-design/icons-vue'
  import { AxiosError } from 'axios'
  import DepartmentFormModal from '@/components/modals/DepartmentFormModal.vue'
  import type { DepartmentItem } from '@/models/DepartmentItem'

  const departments = ref<DepartmentItem[]>([])
  const loading = ref(true)
  const expandedDepartments = ref<Set<number>>(new Set())

  const columns = [
    { title: 'Наименование', dataIndex: 'name' },
    { title: 'Примечание', dataIndex: 'description' },
    { title: 'Действия', dataIndex: 'actions' },
  ]

  interface FlattenedDepartmentItem extends DepartmentItem {
    indent: number
    isParent: boolean
    expanded: boolean
    hasSubDepartments: boolean
  }

  const flattenDepartments = (
    items: DepartmentItem[],
    indent: number = 0,
    parentExpanded: boolean = true
  ): FlattenedDepartmentItem[] => {
    let result: FlattenedDepartmentItem[] = []
    for (const item of items) {
      const subDeps: DepartmentItem[] = item.subDepartments ?? []
      const isParent: boolean = subDeps.length > 0
      const expanded: boolean = expandedDepartments.value.has(item.id)
      if (parentExpanded) {
        result.push({
          ...item,
          indent: indent,
          isParent: isParent,
          expanded: expanded,
          hasSubDepartments: item.subDepartments && item.subDepartments.length > 0,
        })
        if (isParent && expanded) {
          result = result.concat(flattenDepartments(item.subDepartments, indent + 1, expanded))
        }
      }
    }
    return result
  }

  const flattenedData = computed(() => {
    return flattenDepartments(departments.value, 0, true)
  })

  const toggleExpand = (record: FlattenedDepartmentItem) => {
    if (expandedDepartments.value.has(record.id)) {
      expandedDepartments.value.delete(record.id)
    } else {
      expandedDepartments.value.add(record.id)
    }
  }

  onMounted(async () => {
    try {
      departments.value = await departmentApi.getAll()
    } catch (error) {
      console.error('Ошибка загрузки данных:', error)
    } finally {
      loading.value = false
    }
  })

  const open = ref(false)
  const isEditMode = ref(false)
  const selectedDepartment = ref<DepartmentItem | null>(null)

  const openAddModal = () => {
    isEditMode.value = false
    selectedDepartment.value = null
    open.value = true
  }

  const openEditModal = (record: DepartmentItem) => {
    isEditMode.value = true
    selectedDepartment.value = { ...record }
    open.value = true
  }

  const closeFormModal = () => {
    open.value = false
  }

  const handleFormSubmit = async () => {
    try {
      departments.value = await departmentApi.getAll()
      message.success(
        isEditMode.value ? 'Запись об отделе обновлена' : 'Запись об отделе добавлена'
      )
    } catch (error) {
      console.error('Ошибка после сохранения (обновление данных):', error)
      message.error('Ошибка обновления данных')
    } finally {
      open.value = false
    }
  }

  const isDeleteModalVisible = ref(false)

  const openDeleteModal = (record: DepartmentItem) => {
    selectedDepartment.value = record
    isDeleteModalVisible.value = true
  }

  const closeDeleteModal = () => {
    isDeleteModalVisible.value = false
  }

  const confirmDelete = async () => {
    if (!selectedDepartment.value) return
    try {
      await departmentApi.delete(selectedDepartment.value.id)
      departments.value = departments.value.filter(
        (item) => item.id !== selectedDepartment.value!.id
      )
      message.success('Отдел удалён')
    } catch (error) {
      if (error instanceof AxiosError) {
        if (error.response?.status === 409) {
          message.error('Отдел не удалён, так как в этом отделе есть сотрудники', 6)
        } else {
          console.error('Ошибка удаления:', error)
          message.error('Ошибка удаления')
        }
      }
    } finally {
      isDeleteModalVisible.value = false
      departments.value = await departmentApi.getAll()
    }
  }
</script>

<style scoped>
  .department-page {
    position: relative;
    padding: 16px;
  }

  .table-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
</style>

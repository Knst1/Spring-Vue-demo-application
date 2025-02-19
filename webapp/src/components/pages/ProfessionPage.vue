<template>
  <div class="profession-page">
    <LoadingSpinner v-if="loading" />

    <a-table v-else :columns="columns" :data-source="data" :pagination="false" bordered>
      <template #title>
        <div class="table-title">
          <span>Профессии</span>
          <a-button type="primary" @click="openAddModal">Добавить</a-button>
        </div>
      </template>

      <template #bodyCell="{ column, record }">
        <span v-if="column.dataIndex !== 'actions'">
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

    <ProfessionFormModal
      v-if="open"
      :open="open"
      :profession="selectedProfession"
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
      <p>Вы уверены, что хотите удалить профессию "{{ selectedProfession?.name }}"?</p>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
  import { ref, onMounted } from 'vue'
  import { professionApi } from '@/api/ProfessionApi'
  import LoadingSpinner from '@/components/LoadingSpinner.vue'
  import { message } from 'ant-design-vue'
  import { EditOutlined, DeleteOutlined } from '@ant-design/icons-vue'
  import ProfessionFormModal from '@/components/modals/ProfessionFormModal.vue'
  import { AxiosError } from 'axios'
  import type { ProfessionItem } from '@/models/ProfessionItem'

  const data = ref<ProfessionItem[]>([])
  const loading = ref(true)

  const columns = [
    { title: 'Наименование', dataIndex: 'name' },
    { title: 'Примечание', dataIndex: 'description' },
    { title: 'Действия', dataIndex: 'actions' },
  ]

  onMounted(async () => {
    try {
      data.value = await professionApi.getAll()
    } catch (error) {
      console.error('Ошибка загрузки данных:', error)
    } finally {
      loading.value = false
    }
  })

  const open = ref(false)
  const isEditMode = ref(false)
  const selectedProfession = ref<ProfessionItem | null>(null)

  const openAddModal = () => {
    isEditMode.value = false
    selectedProfession.value = null
    open.value = true
  }

  const openEditModal = (record: ProfessionItem) => {
    isEditMode.value = true
    selectedProfession.value = { ...record }
    open.value = true
  }

  const closeFormModal = () => {
    open.value = false
  }

  const handleFormSubmit = async (profession: ProfessionItem) => {
    try {
      if (isEditMode.value && selectedProfession.value) {
        const updated = profession
        data.value = data.value.map((item) => (item.id === updated.id ? updated : item))
        message.success('Запись о профессии обновлена')
      } else {
        const added = profession
        data.value.push(added)
        message.success('Запись о профессии добавлена')
      }
    } catch (error) {
      console.error('Ошибка сохранения:', error)
    } finally {
      open.value = false
    }
  }

  const isDeleteModalVisible = ref(false)

  const openDeleteModal = (record: ProfessionItem) => {
    selectedProfession.value = record
    isDeleteModalVisible.value = true
  }

  const closeDeleteModal = () => {
    isDeleteModalVisible.value = false
  }

  const confirmDelete = async () => {
    if (!selectedProfession.value) return
    try {
      await professionApi.delete(selectedProfession.value.id)
      data.value = data.value.filter((item) => item.id !== selectedProfession.value!.id)
      message.success('Профессия удалена')
    } catch (error) {
      if (error instanceof AxiosError) {
        if (error.response?.status === 409) {
          message.error('Профессия не удалена, так как существуют сотрудники с такой профессией', 6)
        } else {
          console.error('Ошибка удаления:', error)
          message.error('Ошибка удаления')
        }
      }
    } finally {
      isDeleteModalVisible.value = false
    }
  }
</script>

<style scoped>
  .profession-page {
    position: relative;
    padding: 16px;
  }

  .table-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
</style>

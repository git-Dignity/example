<template>
  <table class="static-table">
    <tr v-for="(list, key) in data" :key="key">
      <template v-for="(item, itemKey) in list">
        <td class="labels" :key="item.labels">{{ item.labels }}</td>
        <td class="value" :colspan="item.colspan" :width="item.width" :key="itemKey">
          // 数组
          <div>
            <div v-for="(file, fileIndex) in item.value" :key="fileIndex"></div>
          </div>

          // 附件
          <div class="file-content" v-if="item.isFile">
            <div class="file-content-block" v-for="(file, fileIndex) in item.value" :key="fileIndex">
              <span class="file-content-block__text" title="点击可下载" @click="clickName(file)">{{ file.originalName }}</span>
              <el-image
                v-if="['png', 'jpg', , 'jpeg', 'gif'].includes(file.extension)"
                style="width: 50px; height: 50px"
                :src="file.src"
                fit="fill"
                :preview-src-list="[file.src]"
                @click="handleClickItem"
              >
              </el-image>

              <div v-else style="height: 50px"></div>
            </div>
          </div>
          // 按钮
          <div v-else-if="item.btn && item.btn.isShow" :style="item.btn.style">
            <span>{{ item.value }}</span>
            <div>
              <i v-if="item.btn.icon" :class="item.btn.icon.class" :style="item.btn.icon.style"></i>
              <el-button :type="item.btn.type || 'text'" size="small" plain @click="$emit('btnClick', item)">{{ item.btn.name }}</el-button>
            </div>
          </div>

          // 最简单的纯文字
          <span v-else>{{ item.value }}</span>
        </td>
      </template>
    </tr>
  </table>
</template>

<script>
export default {
  props: {
    data: {
      type: Array,
      default: () => [],
    },
  },
  methods: {
    // 点击文件名字，进行下载
    clickName(file) {
      this.$emit('click', file)
    },
    // 关闭el-image遮罩层
    handleClickItem() {
      this.$nextTick(() => {
        // 获取遮罩层dom
        let domImageMask = document.querySelectorAll('.el-image-viewer__mask')
        if (!domImageMask.length) {
          return
        }
        domImageMask.forEach((d) => {
          d.addEventListener('click', () => {
            // 点击遮罩层时调用关闭按钮的 click 事件
            d.nextElementSibling.click()
          })
        })
      })
    },
  },
}
</script>
<style scoped lang='scss'>
.static-table {
  width: 100%;
  border-left: 1px solid #e5e5e5;
  border-collapse: collapse;
  color: #333;
  margin-bottom: 20px;
}

.static-table tr td {
  border-bottom: 1px solid #e5e5e5;
  border-right: 1px solid #e5e5e5;
  border-top: 1px solid #e5e5e5;
  padding: 15px 12px;
  background: white;
}

.static-table .labels {
  width: 160px;
  vertical-align: middle;
  font-weight: 400;
  background-color: #f8f8f8;
}

.static-table .el-radio__input.is-disabled.is-checked .el-radio__inner::after {
  background-color: #a3b8dd;
  width: 10px;
  height: 10px;
}

.static-table .el-input.is-disabled .el-input__icon {
  display: none;
}

.el-static-table thead tr th {
  color: #333;
  background-color: #f8f8f8;
  font-weight: normal;
}

.static-table .el-checkbox__input.is-disabled.is-checked .el-checkbox__inner:after {
  border-color: #a3b8dd;
}

.file-content {
  display: flex;
  align-items: center;
  &-block {
    padding: 10px;
    border: 1px solid #f3f3f3;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 10px;
    &__text {
      margin-right: 10px;
      cursor: pointer;
    }
  }
}
</style>

<!--
 * @Description: 瀑布流 -- 默认四列
-->
<template>
  <div class="WaterfallFlow">
    <slot></slot>
  </div>
</template>

<script>
export default {
  name: 'WaterfallFlow',
  props: {
    // 子组件class
    childrenClass: {
      type: String,
      default: '',
    },
    // 列数
    columnCount: {
      type: Number,
      default: 4
    },
    // 间隔
    gap: {
      type: Number,
      default: 20
    },
    // 宽度-百分比
    itemWidth: {
      type: Number,
      default: 23 // 23%
    }
  },
  data() {
    return {}
  },
  created() {},
  computed: {
    getFirstItemHeight() {
      return (index) => {
        const item = this.$slots.default
        return item && item[index] && item[index].elm ? item[index].elm.offsetHeight : 0
      }
    },
  },
  methods: {
    // 循环累加前序行中同列元素的高度和间隔
    // 最终高度 = 所有前序行同列元素高度之和 + 间隔总和
    setEleHeight() {
      const container = document.querySelector('.WaterfallFlow')
      let maxBottom = 0
      this.$slots.default.forEach((child, index) => {
        this.$nextTick(() => {
          const f = child.elm

          const baseIndex = index % this.columnCount // 基础索引 0-3
          const group = Math.floor(index / this.columnCount) // 行号

          // 计算累计高度：前序行高度之和 + 间隔
          let totalHeight = 0
          for (let i = 0; i < group; i++) {
            const prevIndex = baseIndex + i * this.columnCount
            totalHeight += this.getFirstItemHeight(prevIndex) + this.gap
          }

          // 计算横向偏移（将百分比转换为像素）
          const containerWidth = f.parentElement.offsetWidth // 父容器宽度
          const groupOffset = (index % this.columnCount) * this.itemWidth // 百分比偏移
          const baseValues = [1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048]
          const base = groupOffset ? baseValues[index % this.columnCount] : 0
          const translateX = (containerWidth * (groupOffset + base)) / 100 // 将百分比转换为精确像素值

          //   f.style.top = `${totalHeight}px`
          f.style.transform = `translate(${translateX}px, ${totalHeight}px)`

          // 新增高度计算
          const itemBottom = totalHeight + f.offsetHeight // 之前的高度 + 自己的高度
          if (itemBottom > maxBottom) {
            maxBottom = itemBottom
          }
          container.style.height = `${maxBottom + 50}px` // 增加50px缓冲
        })
      })
    },
    // 新增防抖函数
    debounce(fn, delay) {
      let timeoutId
      return (...args) => {
        clearTimeout(timeoutId)
        timeoutId = setTimeout(() => fn.apply(this, args), delay)
      }
    },
    handleResize() {
      this.debounce(this.setEleHeight, 200)()
    },
  },
  mounted() {
    this.setEleHeight()
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
  },
}
</script>
<style scoped lang='scss'>
.WaterfallFlow {
  width: 100%;
  gap: 30px;
  position: relative;
  &-item {
    // 这里不生效，子组件会设置
    width: 23%;
    position: absolute;
    left: 0;
    top: 0;
    // 添加GPU加速
    will-change: transform;
    // 优化过渡效果
    transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }
}
</style>

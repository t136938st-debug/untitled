package org.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.common.Result;
import org.example.entity.DiningTable;
import org.example.service.DiningTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 餐桌Controller
 */
@RestController
@RequestMapping("/diningTable")
public class DiningTableController {

    @Autowired
    private DiningTableService diningTableService;

    /**
     * 新增餐桌
     */
    @PostMapping
    public Result<String> add(@RequestBody DiningTable diningTable) {
        diningTableService.save(diningTable);
        return Result.success();
    }

    /**
     * 修改餐桌信息
     */
    @PutMapping
    public Result<String> update(@RequestBody DiningTable diningTable) {
        diningTableService.updateById(diningTable);
        return Result.success();
    }

    /**
     * 删除餐桌
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        diningTableService.removeById(id);
        return Result.success();
    }

    /**
     * 查询所有餐桌列表
     */
    @GetMapping("/list")
    public Result<List<DiningTable>> list() {
        List<DiningTable> list = diningTableService.list();
        return Result.success(list);
    }

    /**
     * 根据ID查询餐桌详情
     */
    @GetMapping("/{id}")
    public Result<DiningTable> getById(@PathVariable Long id) {
        DiningTable table = diningTableService.getById(id);
        return Result.success(table);
    }

    /**
     * 查询空闲餐桌列表
     */
    @GetMapping("/free")
    public Result<List<DiningTable>> listFree() {
        LambdaQueryWrapper<DiningTable> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DiningTable::getStatus, 0);
        List<DiningTable> list = diningTableService.list(queryWrapper);
        return Result.success(list);
    }
}

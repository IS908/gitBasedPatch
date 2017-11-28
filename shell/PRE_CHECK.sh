#!/usr/bin/env bash

# 单独的job检测redis、zk、oracle等是否启动成功

# 1、检测redis
# ps -ef | grep redis | grep -v 'grep' | wc -l

# 2、检测zk
# ps -ef | grep QuorumPeerMain | grep -v 'grep' | wc -l

# 3、检测oracle
# 待定？？？

REDIS_SERVER=57.25.2.111
ZK_SERVER=57.25.2.111
ORACLE_SERVER=57.25.2.111

# Redis状态检查
for ips in ${REDIS_SERVER}
do
    echo Redis Node: ${ips} check ...

done

# ZK状态检查
for ips in ${ZK_SERVER}
do
    echo ZK Node: ${ips} check ...
    ps -ef |
done

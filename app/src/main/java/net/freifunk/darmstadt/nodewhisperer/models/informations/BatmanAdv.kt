package net.freifunk.darmstadt.nodewhisperer.models.informations

class BatmanAdv {
    val vpnConnected: Boolean
    val tq: Int
    val originators: Int
    val neighbors: Int
    val clients: Int
    val throughputKbps: Long

    /* Batman V populates throughputKbps and leaves tq at 0; Batman IV is
     * the inverse. A working gateway therefore requires tq > 0 OR
     * throughputKbps > 0. */
    val hasGateway: Boolean
        get() = tq > 0 || throughputKbps > 0

    constructor(vpnConnected: Boolean, tq: Int, originators: Int, neighbors: Int, clients: Int, throughputKbps: Long = 0) {
        this.vpnConnected = vpnConnected
        this.tq = tq
        this.originators = originators
        this.neighbors = neighbors
        this.clients = clients
        this.throughputKbps = throughputKbps
    }

    constructor(batmanAdv: ByteArray) {
        vpnConnected = batmanAdv[0] == 1.toByte()

        /* Byte 1 is the TQ (Batman IV only; 0 on Batman V) */
        val tq1 = batmanAdv[1].toUByte().toUInt()
        tq = tq1.toInt()

        /* Byte 2 and 3 are the number of originators (Big Endian) */
        originators = (batmanAdv[2].toInt() shl 8) or (batmanAdv[3].toInt() and 0xff)

        /* Byte 4 and 5 are the number of neighbors (Big Endian) */
        neighbors = (batmanAdv[4].toInt() shl 8) or (batmanAdv[5].toInt() and 0xff)

        /* Byte 6 and 7 are the number of clients (Big Endian) */
        clients = (batmanAdv[6].toInt() shl 8) or (batmanAdv[7].toInt() and 0xff)

        /* Bytes 8..11 are the gateway throughput in kbps (Batman V only,
         * uint32 Big Endian). Absent on pre-Batman-V firmware. */
        throughputKbps = if (batmanAdv.size >= 12) {
            ((batmanAdv[8].toLong() and 0xff) shl 24) or
                ((batmanAdv[9].toLong() and 0xff) shl 16) or
                ((batmanAdv[10].toLong() and 0xff) shl 8) or
                (batmanAdv[11].toLong() and 0xff)
        } else {
            0L
        }
    }
}